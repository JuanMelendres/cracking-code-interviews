// Generics: what problem they solve, and how to read `<T>`.
//
// Verified by `tsc -p tsconfig.json` compiling this file with zero errors —
// see tsc-output-after-fix.txt (the whole-project run, this file included).
// The advanced use of this exact pattern for a generic React component
// (`List<T>`) is covered in react-typescript.md (F-119) - this file is
// its prerequisite.

// --- The problem generics solve -------------------------------------------
//
// Without generics, you'd either duplicate this function per type:
//
//   function identityString(value: string): string { return value; }
//   function identityNumber(value: number): number { return value; }
//
// ...or widen it to `any`, which compiles but throws away all type safety:
//
//   function identityAny(value: any): any { return value; }
//   const result = identityAny(42);
//   result.toUpperCase(); // compiles even though this crashes at runtime -
//                         // `any` told the compiler to stop checking.

// --- A generic function: one definition, the type is inferred per call ---

// `<T>` declares a type PARAMETER - a placeholder filled in at each call
// site based on the argument actually passed. Read `identity<T>(value: T): T`
// as "for whatever type T the caller passes in, return that same type."
function identity<T>(value: T): T {
  return value;
}

const identityResult1 = identity(42); // T inferred as `number`
const identityResult2 = identity("hello"); // T inferred as `string`
// identityResult1.toUpperCase(); // tsc rejects: Property 'toUpperCase' does
// not exist on type 'number'. -- proving `T` is REAL and specific per call,
// not silently `any`.
console.log(identityResult1.toFixed(0));
console.log(identityResult2.toUpperCase());

// --- A generic container type --------------------------------------------

interface Box<T> {
  value: T;
  unwrap(): T;
}

function makeBox<T>(value: T): Box<T> {
  return {
    value,
    unwrap() {
      return value;
    },
  };
}

const numberBox = makeBox(7); // Box<number>
const stringBox = makeBox("wrapped"); // Box<string>
console.log(numberBox.unwrap() + 1); // safe - `unwrap()` returns `number` here
console.log(stringBox.unwrap().length); // safe - `unwrap()` returns `string` here

// --- A generic constraint: "T must have at least this shape" -------------

interface HasId {
  id: string | number;
}

// `<T extends HasId>` restricts what `T` can be - only types that
// structurally satisfy `HasId` are accepted - while still letting the
// function work with ANY such type, not one hardcoded shape.
function describeById<T extends HasId>(item: T): string {
  return `item #${item.id}`;
}

console.log(describeById({ id: 1, name: "Ana" }));
console.log(describeById({ id: "sku-42", price: 19.99 }));
// describeById({ name: "no id field" }); // tsc rejects: Property 'id' is
// missing in type '{ name: string; }' but required in type 'HasId'.
