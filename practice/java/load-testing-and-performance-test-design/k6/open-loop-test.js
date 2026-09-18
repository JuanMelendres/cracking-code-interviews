import http from 'k6/http';

// A real open-loop load generator: k6's constant-arrival-rate executor
// fires requests on a fixed schedule (here, 40 iterations/second) regardless
// of how long the previous response took -- the real mechanism that avoids
// coordinated omission, contrasted directly against
// ../src/ClosedLoopLoadGenerator.java against the identical target server.
export const options = {
  scenarios: {
    open_loop: {
      executor: 'constant-arrival-rate',
      rate: 40,
      timeUnit: '1s',
      duration: '10s',
      preAllocatedVUs: 100,
      maxVUs: 300,
    },
  },
};

export default function () {
  http.get(`http://localhost:${__ENV.TARGET_PORT}/work`);
}
