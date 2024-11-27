import http from "k6/http";
import { check, fail, group } from "k6";
import { SharedArray } from "k6/data";
import { FormData } from "k6/encoding";
// Test configuration

export const options = {
  scenarios: {
    concurrent_users: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        { duration: "10s", target: 10 },
        { duration: "2s", target: 5 },
        { duration: "5s", target: 5 },
        { duration: "10s", target: 0 },
      ],
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.25"], // Allow up to 25% failure rate
    http_req_duration: ["p(95)<2000"], // 95% of requests should be below 2s
  },
};
const importFileContent = open("./upload.yml", "b");
if (!importFileContent) {
  fail("Failed to load upload.yml");
}

const BASE_URL = "http://localhost:8080/api";
// Test users with pre-registered credentials
const testUsers = new SharedArray("users", function () {
  return [
    { username: "admin", password: "qwerty" },
    { username: "useradmin", password: "qwerty" },
  ];
});

// Shared route name for concurrent update/delete tests
const SHARED_ROUTE_NAME = "SharedRoute_For_Testing";

// Setup function to create a shared route before tests
export function setup() {
  const responses = testUsers.map((user) => {
    const token = login(user.username, user.password);
    const response = createRoute(token, SHARED_ROUTE_NAME);
    return { user, token, response };
  });

  // Filter successful creations
  const successfulCreations = responses.filter(
    (r) => r.response.status === 201
  );

  if (successfulCreations.length === 0) {
    fail("Failed to create the shared route for testing.");
  }

  // Return the route ID of the first successful creation
  return {
    sharedRouteId: successfulCreations[0].response.json("id"),
    sharedRouteToken: successfulCreations[0].token, // Keep token for re-creation
  };
}
// Helper function for login
function login(username, password) {
  const loginRes = http.post(
    `${BASE_URL}/auth/login`,
    JSON.stringify({
      username: username,
      password: password,
    }),
    {
      headers: { "Content-Type": "application/json" },
      timeout: "10s",
    }
  );

  if (
    !check(loginRes, {
      "login successful": (r) => r.status === 200,
    })
  ) {
    fail(`Login failed for user ${username}`);
  }

  return loginRes.json("token");
}

// Helper function to create a route
function createRoute(token, routeName) {
  const route = {
    name: routeName,
    coordinates: {
      x: Math.floor(Math.random() * 10),
      y: Math.floor(Math.random() * 10),
    },
    from: { x: 0, y: 0, name: "Start" },
    to: { x: 100, y: 100, name: "End" },
    distance: 150,
    rating: Math.floor(Math.random() * 5) + 1,
    allowAdminEditing: true,
  };

  return http.post(`${BASE_URL}/routes`, JSON.stringify(route), {
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    timeout: "10s",
  });
}

// Helper function to import routes
function importRoutes(token, fileContent) {
  const payload = {
    file: http.file(fileContent, "upload.yml"),
  };
  return http.post(`${BASE_URL}/routes/import`, payload, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    timeout: "60s",
  });
}

// Main test function
export default function (data) {
  let routeId = data.sharedRouteId;
  const token = data.sharedRouteToken;
  group("Concurrent Updates on Shared Route", () => {
    const updatedName = `UpdatedRoute_${Math.random()
      .toString(36)
      .substring(7)}`;
    const response = http.put(
      `${BASE_URL}/routes/${routeId}`,
      JSON.stringify({
        name: updatedName,
        coordinates: { x: 20, y: 30 },
        from: { x: 0, y: 0, name: "Updated Start" },
        to: { x: 100, y: 100, name: "Updated End" },
        distance: 200,
        rating: 5,
        allowAdminEditing: true,
      }),
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        timeout: "10s",
      }
    );
    check(response, {
      "shared route update handled correctly": (r) =>
        r.status === 200 || r.status === 400,
    });
  });

  // Use the updated `routeId` in subsequent tests
  group("Concurrent Creation of Routes with Duplicate Names", () => {
    const response = createRoute(token, SHARED_ROUTE_NAME);
    check(response, {
      "duplicate route creation handled correctly": (r) =>
        r.status === 201 || r.status === 409,
    });
    if (response.status === 201) {
      routeId = response.json("id");
    }
    if (response.status !== 409 && response.status !== 201) {
      console.log("Duplicate route creation failed");
      console.log(response.status);
      console.log(response.body);
    }
  });

  group("Test Transactional Isolation During Import", () => {
    const importResponse = importRoutes(token, importFileContent);
    check(importResponse, {
      "routes import success or conflict": (r) =>
        r.status === 200 || r.status === 400,
    });
  });
}
