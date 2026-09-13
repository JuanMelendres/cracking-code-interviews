// Demo: array/object literals, map/filter/reduce/forEach, destructuring, spread/rest.

console.log('--- DEMO: array methods ---');

const scores = [72, 95, 58, 81, 40, 99];

const passing = scores.filter((score) => score >= 60);
const letterGrades = passing.map((score) => (score >= 90 ? 'A' : score >= 80 ? 'B' : 'C'));
const total = scores.reduce((sum, score) => sum + score, 0);

console.log('scores        ->', scores);
console.log('passing       ->', passing);
console.log('letterGrades  ->', letterGrades);
console.log('total (reduce)->', total);

const logged = [];
scores.forEach((score) => logged.push(`logged:${score}`));
console.log('forEach side effect ->', logged);

console.log('\n--- DEMO: destructuring ---');

const person = { name: 'Ana', age: 29, address: { city: 'Bogota', zip: '110111' } };
const { name, age, address: { city } } = person;
console.log('destructured name, age, city ->', name, age, city);

const [firstScore, secondScore, ...restScores] = scores;
console.log('firstScore, secondScore, restScores ->', firstScore, secondScore, restScores);

function describePerson({ name: personName, age: personAge = 0 }) {
  return `${personName} is ${personAge}`;
}
console.log('destructured parameter with default ->', describePerson({ name: 'Bilal' }));

console.log('\n--- DEMO: spread and rest ---');

const originalArray = [1, 2, 3];
const copiedAndExtended = [...originalArray, 4, 5];
console.log('spread to copy+extend array ->', copiedAndExtended, '| original unchanged ->', originalArray);

const originalObject = { a: 1, b: 2 };
const mergedObject = { ...originalObject, b: 99, c: 3 };
console.log('spread to merge objects (later key wins) ->', mergedObject);

function sumAll(...numbers) {
  return numbers.reduce((sum, n) => sum + n, 0);
}
console.log('rest parameters, sumAll(1,2,3,4) ->', sumAll(1, 2, 3, 4));
