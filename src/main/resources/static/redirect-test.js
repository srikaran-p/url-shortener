import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 20,          // virtual users
    duration: '60s',  // test duration
};

// k6 run redirect-test.js
export default function () {
    const url = 'http://localhost:4000/urls/8010fe38';

    const res = http.get(url, {
        redirects: 0,
    });

    check(res, {
        'status is 302': (r) => r.status === 302,
    });

    sleep(1);
}