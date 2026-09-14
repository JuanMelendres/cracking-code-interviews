// Demo: real ESM import/export (this repository's package.json sets "type": "module",
// which is why plain .js extensions here are treated as ES modules by Node).

import describeNumber, { square, circleArea, PI_APPROX } from './mathUtils.js';

console.log('--- DEMO: ESM import/export ---');
console.log('named import square(5)         ->', square(5));
console.log('named import circleArea(2)     ->', circleArea(2));
console.log('named import PI_APPROX         ->', PI_APPROX);
console.log('default import describeNumber(6) ->', describeNumber(6));
