// Demo: `this` binding differs between regular functions and arrow functions.

console.log('--- DEMO: this-in-arrow-vs-regular-function ---');

const counter = {
  count: 0,
  label: 'regularMethod',
  incrementRegular: function () {
    // Regular function: `this` is bound at CALL TIME to whatever called it.
    this.count++;
    console.log('incrementRegular: this.label =', this.label, '| this.count =', this.count);
  },
  incrementArrow: () => {
    // Arrow function: `this` is captured LEXICALLY from the surrounding scope
    // (the module's top level here), NOT from `counter`.
    console.log('incrementArrow: this.label =', this?.label, '| this.count =', this?.count);
  },
};

counter.incrementRegular();
counter.incrementRegular();
counter.incrementArrow();

console.log('\n--- DEMO: the setTimeout callback footgun ---');

const timerObject = {
  name: 'timerObject',
  runWithRegularCallback() {
    setTimeout(function () {
      // Regular function passed as a callback loses `timerObject` as its `this`.
      // Node's timer implementation invokes the callback bound to its internal
      // Timeout object, not to timerObject and not to undefined -- either way,
      // "this.name" here is NOT "timerObject", which is the actual footgun.
      console.log('regular callback: this.name is', this.name, '(this is a Node Timeout object, not timerObject)');
    }, 0);
  },
  runWithArrowCallback() {
    setTimeout(() => {
      // Arrow function inherits `this` from runWithArrowCallback's own `this`,
      // which IS timerObject because it was called as timerObject.runWithArrowCallback().
      console.log('arrow callback: this.name is', this.name);
    }, 0);
  },
};

timerObject.runWithRegularCallback();
timerObject.runWithArrowCallback();

console.log('\n--- DEMO: extracting a method loses `this` ---');

class Button {
  constructor(label) {
    this.label = label;
  }
  handleClickRegular() {
    console.log('handleClickRegular: this.label =', this?.label);
  }
  handleClickArrow = () => {
    console.log('handleClickArrow: this.label =', this?.label);
  };
}

const button = new Button('Submit');
const detachedRegular = button.handleClickRegular;
const detachedArrow = button.handleClickArrow;

// Calling the detached regular method with no receiver: in an ES module (always
// strict mode), a plain function call's `this` is `undefined`, not the global
// object and not `button` — the connection to `button` was lost the moment the
// method was assigned to a bare variable instead of called as `button.method()`.
detachedRegular();

// The arrow-function class field still works detached, because it captured
// `this` lexically at the moment the instance was constructed, not at call time.
detachedArrow();
