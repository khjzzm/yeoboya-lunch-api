import http from 'k6/http';
import { check, sleep } from 'k6';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

// export const options = {
//   stages: [
//     { duration: '10s', target: 10 },
//     { duration: '20s', target: 10 },
//     { duration: '10s', target: 0 },
//   ],
// };
//
export const options = {
  vus: 1000,
  duration: '15s',
};

const BASE_URL = __ENV.API_BASE_URL;
const TOKEN = __ENV.AUTH_TOKEN;

export default function () {
  const res = http.get(`${BASE_URL}/board/free?page=1&size=20`, {
    headers: {
      Authorization: TOKEN,
    },
  });

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(1);
}

export function handleSummary(data) {
  return {
    stdout: textSummary(data, { indent: '→ ', enableColors: true }),
    '/app/results/summary.json': JSON.stringify(data, null, 2),
    '/app/results/summary.html': htmlReport(data),
  };
}