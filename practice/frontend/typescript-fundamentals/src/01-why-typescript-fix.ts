// The same function, in TypeScript, with an explicit `number` annotation.
//
// The exact same mistake at the exact same call site is now a COMPILE-TIME
// error - `tsc` refuses to produce output, and the error names the file,
// line, and exact type mismatch, before the code ever runs.
//
// See tsc-output-before-fix.txt for the real captured error this file
// produces when the bad call (line 15, currently commented out) is active,
// and tsc-output-after-fix.txt for this file compiling clean once it's
// removed.

function formatPrice(amount: number): string {
  return `$${amount.toFixed(2)}`;
}

console.log(formatPrice(19.5)); // "$19.50" - fine
console.log(formatPrice(0)); // "$0.00" - fine
console.log(formatPrice(19)); // "$19.00" - fine

// Uncomment the line below to reproduce the real captured error in
// tsc-output-before-fix.txt. It is commented out here so this file is
// part of the repository's normal, currently-passing compile.
// console.log(formatPrice("19.99"));
