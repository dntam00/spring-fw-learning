import http from 'k6/http';
import { check } from 'k6';

export let options = {
    scenarios: {
        max_load: {
            executor: 'ramping-vus',
            startVUs: 50,
            stages: [
                { duration: '30s', target: 100 }, // Ramp up to 100 VUs in 30s
                { duration: '60s', target: 200 }, // Ramp up to 200 VUs in 60s
                { duration: '30s', target: 1000 }, // Ramp up to 1000 VUs in 30s
                { duration: '60s', target: 1000 }, // Stay at 1000 VUs for 60s
                { duration: '30s', target: 0 },   // Ramp down to 0 VUs
            ],
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'], // Less than 1% errors
        http_req_duration: ['p(95)<3000'], // 95% of requests should be below 3s
    },
};

export default function () {

    let randomDelay = Math.floor(Math.random() * 500) + 100;

    let res = http.get(`http://127.0.0.1:8080/api/delay/${randomDelay}`);

    check(res, {
        'is status 200': (r) => r.status === 200,
        'response time < 2000ms': (r) => r.timings.duration < 2000,
    });
}

export function teardown() {
    console.log('Test completed - maximum load reached');
}