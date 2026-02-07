import http from 'k6/http';

// export const options = {
//     vus: 50,
//     duration: '30s',
// }


export const options = {
    thresholds: {
        http_req_failed: ['rate<0.01'], // http errors should be less than 1%
        http_req_duration: ['p(95)<500'], // 95% of requests should be below 200ms,

    },
    scenarios: {
        my_scenario: {
            executor: 'constant-arrival-rate',
            duration: '60s', // total duration
            preAllocatedVUs: 2000, // to allocate runtime resources     preAll

            rate: 50, // number of constant iterations given `timeUnit`
            timeUnit: '1s',
        },
    },
};

export default function () {

    const url = 'http://localhost:8000/test';


    const params = {

        headers: {

            'Content-Type': 'application/json',

        },

    };


    http.get(url);

}