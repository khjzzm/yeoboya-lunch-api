import http from 'k6/http';
import { check, sleep } from 'k6';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

export const options = {
  vus: 1000,
  duration: '5s',
};

const BASE_URL = 'https://api.yeoboya-lunch.com';

function generateUUIDv4() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = Math.random() * 16 | 0,
      v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

function randomNickname() {
  const base = ['k6User', 'loadTester', 'anon', 'stressGuy', 'benchBot'];
  const suffix = Math.floor(Math.random() * 10000);
  return `${base[Math.floor(Math.random() * base.length)]}_${suffix}`;
}

export default function () {
  const url = `${BASE_URL}/board/anonymous`;

  const payload = JSON.stringify({
    nickname: randomNickname(),
    password: '',
    content: `test post ${__ITER}`,
    clientUUID: generateUUIDv4(),
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(url, payload, params);

  const success = check(res, {
    'status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    'contains content': (r) => r.body.includes('test'),
  });

  if (!success) {
    console.error(`❌ Request failed:
  → Status: ${res.status}
  → Body: ${res.body}
  → Payload: ${payload}`);
  }

  sleep(1);
}

export function handleSummary(data) {
  return {
    stdout: textSummary(data, { indent: '→ ', enableColors: true }),
    '/app/results/summary.html': htmlReport(data),
    '/app/results/summary.json': JSON.stringify(data, null, 2),
  };
}