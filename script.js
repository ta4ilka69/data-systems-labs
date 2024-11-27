import http from 'k6/http';
import { check, sleep, fail, group } from 'k6';
import { SharedArray } from 'k6/data';
import exec from 'k6/execution';

// Test configuration
export const options = {
  scenarios: {
    concurrent_users: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '1s', target: 7 },
        { duration: '20s', target: 7 },
        { duration: '10s', target: 0 },
      ],
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.05'], // Allow up to 5% failure rate
    http_req_duration: ['p(95)<2000'], // 95% of requests should be below 2s
  },
};

const BASE_URL = 'http://localhost:8080/api';

// Test users with pre-registered credentials
const testUsers = new SharedArray('users', function () {
  return [
    { username: 'asdf', password: 'qwerty' },
    { username: 'admin', password: 'qwerty' },
    { username: 'useradmin', password: 'qwerty' },
    { username: 'user', password: 'qwerty' },
    { username: 'user1', password: 'qwerty' },
    { username: 'user2', password: 'qwerty' },
    { username: 'user3', password: 'qwerty' },
  ];
});

// Shared route name for concurrent update/delete tests
const SHARED_ROUTE_NAME = 'SharedRoute_For_Testing';

// Setup function to create a shared route before tests
export function setup() {
  const responses = testUsers.map(user => {
    const token = login(user.username, user.password);
    const response = createRoute(token, SHARED_ROUTE_NAME);
    return { user, token, response };
  });

  // Filter successful creations
  const successfulCreations = responses.filter(r => r.response.status === 201);

  if (successfulCreations.length === 0) {
    fail('Failed to create the shared route for testing.');
  }

  // Return the route ID of the first successful creation
  return {
    sharedRouteId: successfulCreations[0].response.json('id'),
  };
}

// Helper function for login
function login(username, password) {
  const loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
    username: username,
    password: password,
  }), {
    headers: { 'Content-Type': 'application/json' },
    timeout: '10s',
  });

  if (!check(loginRes, {
    'login successful': (r) => r.status === 200,
  })) {
    fail(`Login failed for user ${username}`);
  }

  return loginRes.json('token');
}

// Helper function to create a route
function createRoute(token, routeName) {
  const route = {
    name: routeName,
    coordinates: { x: Math.floor(Math.random() * 10), y: Math.floor(Math.random() * 10) },
    from: { x: 0, y: 0, name: "Start" },
    to: { x: 100, y: 100, name: "End" },
    distance: 150,
    rating: Math.floor(Math.random() * 5) + 1,
    allowAdminEditing: true
  };

  return http.post(`${BASE_URL}/routes`, JSON.stringify(route), {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    timeout: '10s',
  });
}

// Helper function to import routes
function importRoutes(token, filePath) {
  const file = open(filePath, 'b'); // Ensure the file is available in the K6 environment
  return http.post(`${BASE_URL}/routes/import`, file.body, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'Authorization': `Bearer ${token}`
    },
    timeout: '60s',
  });
}

// Main test function
export default function (data) {
  const routeId = data.sharedRouteId;

  // Get random user credentials
  const userCreds = testUsers[exec.vu.iterationInScenario % testUsers.length];
  const token = login(userCreds.username, userCreds.password);

  group('Create Route with Unique Name', () => {
    const uniqueRouteName = `UniqueRoute_${Math.random().toString(36).substring(7)}`;
    const response = createRoute(token, uniqueRouteName);

    check(response, {
      'unique route creation successful': (r) => r.status === 201,
    });
  });

  group('Create Route with Duplicate Name', () => {
    const response = createRoute(token, SHARED_ROUTE_NAME); // Attempt to create a route with the shared name

    check(response, {
      'duplicate route creation handled correctly': (r) => 
        r.status === 201 || r.status === 409, // 400 for duplicate name
    });
  });

  group('Update Shared Route', () => {
    const updatedName = `UpdatedRoute_${Math.random().toString(36).substring(7)}`;
    const response = http.put(`${BASE_URL}/routes/${routeId}`, JSON.stringify({
      name: updatedName,
      coordinates: { x: 20, y: 30 },
      from: { x: 0, y: 0, name: "Updated Start" },
      to: { x: 100, y: 100, name: "Updated End" },
      distance: 200,
      rating: 5,
      allowAdminEditing: true
    }), {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      timeout: '10s',
    });

    check(response, {
      'shared route update handled correctly': (r) => 
        r.status === 200,
    });
  });

  group('Delete Shared Route', () => {
    const response = http.del(`${BASE_URL}/routes/${routeId}`, null, {
      headers: {
        'Authorization': `Bearer ${token}`
      },
      timeout: '10s',
    });

    check(response, {
      'shared route deletion handled correctly': (r) => 
        [204, 404, 400, 200].includes(r.status),
    });
  });

  group('Import Routes Concurrently', () => {
    // Assuming you have a test file named 'import_test.yaml' in the test directory
    const importFilePath = 'upload.yaml';
    const response = importRoutes(token, importFilePath);

    check(response, {
      'import routes handled correctly': (r) => 
        r.status === 200 || r.status === 400,
    });
  });

  sleep(1);
}