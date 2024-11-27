import http from 'k6/http';
import { check, group, sleep } from 'k6';

export const options = {
  vus: 3, // количество виртуальных пользователей
  duration: '1s', // продолжительность теста
};

const BASE_URL = 'http://localhost:8080/api';
const USERNAME = 'asdf';
const PASSWORD = 'qwerty';

export function setup() {
  const loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
    username: USERNAME,
    password: PASSWORD,
  }), 
  { headers: { 'Content-Type': 'application/json' } });

  const authToken = loginRes.json('token');
  check(authToken, { 'logged in successfully': () => authToken !== '' });

  return authToken;
}

export default function (authToken) {
  const headers = {
    'Authorization': `Bearer ${authToken}`,
    'Content-Type': 'application/json',
  };

  sleep(1);
}
