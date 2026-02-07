import http from 'k6/http';
import { check } from 'k6';
import { Counter } from 'k6/metrics';

const accepted = new Counter('accepted_requests');
const rejected = new Counter('rejected_requests');

// ============================================================
// Task Queue Rejection Demo — k6 Load Test
//
// Config: maxThreads=2, taskQueueCapacity=1
// Sends 5 concurrent requests to /api/slow/5:
//   - Requests 1-2: assigned to worker threads
//   - Request 3:    queued in task queue (capacity=1)
//   - Requests 4-5: REJECTED (queue full → connection reset)
// ============================================================

export const options = {
    scenarios: {
        queue_rejection: {
            executor: 'shared-iterations',
            vus: 5,
            iterations: 5,
            maxDuration: '30s',
        },
    },
};

export default function () {
    const res = http.get('http://localhost:8081/api/slow/5');

    const ok = check(res, {
        'status is 200': (r) => r.status === 200,
    });

    if (ok) {
        const body = JSON.parse(res.body);
        accepted.add(1);
        console.log(
            `[VU ${__VU}] ACCEPTED  thread=${body.thread} ` +
            `duration=${body.durationMs}ms active=${body.activeThreads}/${body.poolSize}`
        );
    } else {
        rejected.add(1);
        console.log(
            `[VU ${__VU}] REJECTED  status=${res.status} error=${res.error}`
        );
    }
}

export function handleSummary(data) {
    const acc = data.metrics.accepted_requests
        ? data.metrics.accepted_requests.values.count : 0;
    const rej = data.metrics.rejected_requests
        ? data.metrics.rejected_requests.values.count : 0;

    console.log('\n========== SUMMARY ==========');
    console.log(`Accepted requests: ${acc}`);
    console.log(`Rejected requests: ${rej}`);
    console.log(`Total wall time:   ${(data.state.testRunDurationMs / 1000).toFixed(1)}s`);
    console.log('');
    console.log('Config: maxThreads=2, taskQueueCapacity=1');
    console.log('  2 threads + 1 queued = 3 accepted');
    console.log('  remaining requests   = rejected (connection reset)');
    console.log('==============================\n');

    return {};
}
