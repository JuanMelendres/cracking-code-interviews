// Function types, and optional / readonly properties.
//
// Verified by `tsc -p tsconfig.json` compiling this file with zero errors —
// see tsc-output-after-fix.txt (the whole-project run, this file included).

// --- Function types: describing the SHAPE of a callable value ------------

// A function type describes parameter types and a return type, without
// implementing the function - useful for typing a callback parameter or a
// variable that will hold a function.
type Comparator = (a: number, b: number) => number;

const ascending: Comparator = (a, b) => a - b;
const descending: Comparator = (a, b) => b - a;

const values = [5, 2, 8, 1];
console.log([...values].sort(ascending)); // [1, 2, 5, 8]
console.log([...values].sort(descending)); // [8, 5, 2, 1]

// A function PARAMETER typed as a function type - the common case for
// callbacks (event handlers, array-method callbacks, etc.).
function applyToEach(items: number[], transform: (item: number) => number): number[] {
  return items.map(transform);
}
console.log(applyToEach([1, 2, 3], (n) => n * 10)); // [10, 20, 30]

// --- Optional properties (`?`) --------------------------------------------

interface UserProfile {
  id: number;
  name: string;
  nickname?: string; // may be omitted entirely
}

function displayName(profile: UserProfile): string {
  // TypeScript forces you to account for `nickname` possibly being
  // `undefined` - `profile.nickname` has type `string | undefined`, not
  // `string`, so using it as a plain string without a check is a real
  // compile error.
  return profile.nickname ?? profile.name;
}

console.log(displayName({ id: 1, name: "Ana" }));
console.log(displayName({ id: 2, name: "Bilal Hassan", nickname: "Bee" }));

// --- Readonly properties ----------------------------------------------------

interface ImmutablePoint {
  readonly x: number;
  readonly y: number;
}

const originPoint: ImmutablePoint = { x: 0, y: 0 };
// originPoint.x = 5; // tsc rejects: Cannot assign to 'x' because it is a
// read-only property.
console.log(`originPoint = (${originPoint.x}, ${originPoint.y})`);

// `readonly` is enforced only by the type checker, at compile time - it
// does not freeze the object at runtime the way `Object.freeze()` does.
// Assigning through a differently-typed reference to the SAME object would
// still work, which is worth knowing rather than assuming `readonly`
// provides runtime immutability guarantees.
const mutableAlias: { x: number; y: number } = originPoint;
mutableAlias.x = 99; // compiles - `mutableAlias`'s type has no `readonly`
console.log(`originPoint.x is now ${originPoint.x} through the mutable alias`);
