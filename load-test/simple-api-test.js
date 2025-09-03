import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '5m', target: 100}
    ],
};

export default function () {
    const res = http.get('http://54.180.86.179:8080/api/reviews/2');
    check(res, { 'res status was 200' : (r) => r.status === 200});
    sleep(1);
}