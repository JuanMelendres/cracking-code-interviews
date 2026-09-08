// Demo: == vs === surprises, and why this repository always recommends ===.

console.log('--- DEMO: == vs === surprises ---');

const cases = [
  ['[] == false', [] == false],
  ['[] === false', [] === false],
  ["'0' == false", '0' == false],
  ["'0' === false", '0' === false],
  ['null == undefined', null == undefined],
  ['null === undefined', null === undefined],
  ["'' == 0", '' == 0],
  ["'' === 0", '' === 0],
  ["[] == ''", [] == ''],
  ['[1] == 1', [1] == 1],
  ["'1' == 1", '1' == 1],
  ["'1' === 1", '1' === 1],
];

for (const [expression, result] of cases) {
  console.log(expression.padEnd(20), '->', result);
}

console.log('\n--- WHY: == coerces types before comparing, === never does ---');
console.log('[] == false  coerces both sides toward numbers: [] -> "" -> 0, false -> 0, so 0 == 0 is true.');
console.log("'0' == false coerces false -> 0 and '0' -> 0, so 0 == 0 is true, even though '0' is a truthy string.");
console.log('=== skips coercion entirely and compares type + value directly, which is why this repository');
console.log('always recommends === (and !==): it removes an entire class of "why is this true?!" bugs.');
