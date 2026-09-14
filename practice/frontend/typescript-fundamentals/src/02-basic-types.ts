// Basic types, inference vs. explicit annotation, and `any` vs `unknown`.
//
// Verified by `tsc -p tsconfig.json` compiling this file with zero errors —
// see tsc-output-after-fix.txt (the whole-project run, this file included).

// --- Primitive types, arrays, tuples, enums ------------------------------

let username: string = "ana";
let age: number = 34;
let isActive: boolean = true;

// An array of numbers - either syntax is equivalent.
let scores: number[] = [95, 82, 71];
let names: Array<string> = ["ana", "bilal"];

// A tuple: a FIXED-LENGTH array where each position has its own type.
// Unlike `number[]`, `[string, number]` knows position 0 is a string and
// position 1 is a number - and enforces exactly two elements.
let httpStatus: [number, string] = [404, "Not Found"];
// httpStatus = [404]; // tsc rejects: Source has 1 element(s) but target requires 2.
// httpStatus = ["404", "Not Found"]; // tsc rejects: Type 'string' is not assignable to type 'number'.

// An enum: a named set of related constants.
enum LogLevel {
  Debug,
  Info,
  Warning,
  Error,
}
const currentLevel: LogLevel = LogLevel.Warning;

// --- Inference vs. explicit annotation ------------------------------------

// TypeScript INFERS the type from the initializer - no annotation needed,
// and the compiler still enforces it from this point on.
let inferredCount = 3; // inferred as `number`
// inferredCount = "three"; // tsc rejects: Type 'string' is not assignable to type 'number'.

// An explicit annotation is required when there's no initializer to infer
// from, or when you want to widen/narrow beyond what inference would pick.
let futureValue: number;
futureValue = 42;

// --- `any` vs `unknown` ----------------------------------------------------

// `any` opts a value COMPLETELY out of type checking - every operation on
// it is allowed, and the compiler will never catch a mistake here again.
let dangerousValue: any = fetchFromSomewhereUntyped();
console.log(dangerousValue.toFixed(2)); // compiles - even if this crashes at runtime
console.log(dangerousValue.thisMethodDoesNotExist()); // ALSO compiles - `any` disables checking entirely

// `unknown` accepts anything (like `any`), but FORBIDS using it until you
// narrow its type first - it keeps the compiler's safety net intact.
let safeValue: unknown = fetchFromSomewhereUntyped();
// console.log(safeValue.toFixed(2)); // tsc rejects: Object is of type 'unknown'.
if (typeof safeValue === "number") {
  // Inside this branch, TypeScript has narrowed `safeValue` to `number`.
  console.log(safeValue.toFixed(2)); // compiles - proven safe by the check above
}

function fetchFromSomewhereUntyped(): any {
  return 19.99;
}
