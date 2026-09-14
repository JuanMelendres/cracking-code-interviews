// Demo: event-loop ordering — synchronous code, microtasks (Promises), and
// macrotasks (setTimeout) do not run in the order they appear in the source.

console.log('--- DEMO: sync vs microtask vs macrotask ordering ---');

console.log('1: sync - start');

setTimeout(() => {
  console.log('5: macrotask - setTimeout(fn, 0) callback');
}, 0);

Promise.resolve().then(() => {
  console.log('3: microtask - first .then()');
});

Promise.resolve().then(() => {
  console.log('4: microtask - second .then()');
});

console.log('2: sync - end');

console.log('\n--- WHY setTimeout(fn, 0) DOES NOT RUN IMMEDIATELY ---');
console.log('The call stack must fully empty, THEN the microtask queue must fully');
console.log('drain, and only THEN does the event loop pull the next macrotask off');
console.log('the timer queue. Expected order printed above: 1, 2, 3, 4, 5.');

console.log('\n--- DEMO: async/await is sugar over the same microtask queue ---');

async function asyncDemo() {
  console.log('A: inside asyncDemo, before await');
  await null;
  console.log('C: inside asyncDemo, after await (queued as a microtask)');
}

console.log('start of async/await demo');
asyncDemo();
console.log('B: synchronous code right after calling asyncDemo() (runs before C)');
