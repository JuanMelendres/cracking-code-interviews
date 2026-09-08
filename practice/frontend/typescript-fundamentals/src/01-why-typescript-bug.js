// A real, runnable JavaScript bug that a type would have caught.
//
// `formatPrice` is written assuming `amount` is always a number. Nothing in
// plain JS enforces that. Run this file with Node to see it actually throw:
//
//   node src/01-why-typescript-bug.js
//
// Every call up to the last one works. The last one blows up at runtime,
// in production, on whichever request happens to hit it first — not at
// the moment the bad value was introduced.

function formatPrice(amount) {
  return `$${amount.toFixed(2)}`;
}

console.log(formatPrice(19.5)); // "$19.50" - fine
console.log(formatPrice(0)); // "$0.00" - fine
console.log(formatPrice(19)); // "$19.00" - fine

// Somewhere upstream, a query-string parameter, a form field, or a JSON
// payload from an API that changed its contract hands this function a
// STRING that merely looks like a price. Nothing about calling
// formatPrice("19.99") looks wrong at the call site.
console.log(formatPrice("19.99"));
