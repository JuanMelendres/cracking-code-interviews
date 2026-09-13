// Demo: a real closure-based counter factory.

console.log('--- DEMO: closure-based counter factory ---');

function makeCounter(startingValue) {
  let count = startingValue;
  return {
    increment() {
      count++;
      return count;
    },
    decrement() {
      count--;
      return count;
    },
    getValue() {
      return count;
    },
  };
}

const counterA = makeCounter(0);
const counterB = makeCounter(100);

console.log('counterA.increment() ->', counterA.increment());
console.log('counterA.increment() ->', counterA.increment());
console.log('counterA.increment() ->', counterA.increment());
console.log('counterB.increment() ->', counterB.increment());
console.log('counterA.getValue()  ->', counterA.getValue());
console.log('counterB.getValue()  ->', counterB.getValue());
console.log('counterA and counterB are independent closures over separate `count` variables.');

console.log('\n--- DEMO: the classic var-in-a-loop closure bug, and the let fix ---');

const varCallbacks = [];
for (var i = 0; i < 3; i++) {
  varCallbacks.push(function () {
    return i;
  });
}
console.log('with var:', varCallbacks.map((fn) => fn()));

const letCallbacks = [];
for (let j = 0; j < 3; j++) {
  letCallbacks.push(function () {
    return j;
  });
}
console.log('with let:', letCallbacks.map((fn) => fn()));

console.log('\n--- WHY THIS MATTERS FOR REACT: useState relies on the same closure mechanism ---');

function createFakeUseStateHook(initialValue) {
  let state = initialValue;
  const listeners = [];
  function useState() {
    function setState(newValue) {
      state = newValue;
      listeners.forEach((listener) => listener(state));
    }
    return [state, setState];
  }
  function subscribe(listener) {
    listeners.push(listener);
  }
  return { useState, subscribe };
}

const fakeHook = createFakeUseStateHook(0);
fakeHook.subscribe((newState) => console.log('fake component re-rendered with state =', newState));
const [initialState, setState] = fakeHook.useState();
console.log('initial fake state ->', initialState);
setState(1);
setState(2);
