// Demo: var vs let vs const, block scope vs function scope, temporal dead zone.

console.log('--- DEMO: var is function-scoped, let/const are block-scoped ---');

function scopeDemo() {
  if (true) {
    var functionScoped = 'visible outside the if-block';
    let blockScoped = 'visible only inside the if-block';
    console.log('inside block: functionScoped =', functionScoped, '| blockScoped =', blockScoped);
  }
  console.log('outside block: functionScoped =', functionScoped);
  try {
    console.log('outside block: blockScoped =', blockScoped);
  } catch (error) {
    console.log('outside block: accessing blockScoped threw', error.constructor.name, '-', error.message);
  }
}
scopeDemo();

console.log('\n--- DEMO: temporal dead zone ---');

function tdzDemo() {
  try {
    console.log(beforeDeclaration);
  } catch (error) {
    console.log('reading a `let` before its declaration threw', error.constructor.name, '-', error.message);
  }
  let beforeDeclaration = 'now initialized';
  console.log('after declaration:', beforeDeclaration);
}
tdzDemo();

console.log('\n--- DEMO: const prevents reassignment, not mutation ---');

const fixedBinding = { count: 0 };
fixedBinding.count = 1;
console.log('mutating a const object is allowed ->', fixedBinding);

try {
  fixedBinding = { count: 99 };
} catch (error) {
  console.log('reassigning a const binding threw', error.constructor.name, '-', error.message);
}

console.log('\n--- DEMO: typeof and truthy/falsy ---');

const falsyValues = [0, '', null, undefined, NaN, false];
const truthyExamples = ['0', [], {}, ' ', 1];

console.log('typeof examples:', typeof 42, typeof 'x', typeof true, typeof undefined, typeof {}, typeof [], typeof function () {});
console.log('falsy values are falsy:', falsyValues.map((v) => Boolean(v)));
console.log('these look "empty" but are truthy:', truthyExamples.map((v) => Boolean(v)));
