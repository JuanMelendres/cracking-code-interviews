// Interfaces vs. type aliases, and structural typing ("duck typing").
//
// Verified by `tsc -p tsconfig.json` compiling this file with zero errors —
// see tsc-output-after-fix.txt (the whole-project run, this file included).

// --- Interface vs. type alias: mostly interchangeable for object shapes --

interface UserInterface {
  id: number;
  name: string;
}

type UserTypeAlias = {
  id: number;
  name: string;
};

// Both are used identically at the call site.
function greetInterface(user: UserInterface): string {
  return `Hello, ${user.name}`;
}
function greetAlias(user: UserTypeAlias): string {
  return `Hello, ${user.name}`;
}

// The practical differences:
// 1. `interface` can be re-opened and merged (declaration merging) - two
//    `interface Foo { ... }` blocks in the same scope combine into one.
//    A `type` alias cannot be redeclared at all - it's a compile error.
// 2. `type` can alias ANY type, not just object shapes: unions,
//    intersections, tuples, primitives (`type ID = string | number;`).
//    `interface` can only describe object/class shapes.
// Rule of thumb used in this chapter: prefer `interface` for public object
// shapes (props, API response shapes) that might need to be extended;
// reach for `type` for unions, tuples, and anything that isn't a plain
// object shape.

// --- Structural typing: TypeScript checks SHAPE, not declared identity ---

// `Point` and `Coordinate` are two DIFFERENT, unrelated type declarations.
// Nothing declares that one implements or extends the other.
interface Point {
  x: number;
  y: number;
}

interface Coordinate {
  x: number;
  y: number;
}

const coordinate: Coordinate = { x: 3, y: 4 };

// This compiles - proven directly here, not just asserted. TypeScript does
// not check that `coordinate` was declared as a `Coordinate`; it checks
// only that the VALUE has every property `Point` requires, with compatible
// types. This is TypeScript's real duck-typing model: "if it has an `x`
// and a `y`, both numbers, it IS a Point," regardless of what interface
// name it was originally declared under.
const asPoint: Point = coordinate;
console.log(`asPoint = (${asPoint.x}, ${asPoint.y})`);

// An object literal with an EXTRA property also structurally satisfies
// `Point` when assigned through a variable first...
const coordinateWithLabel = { x: 1, y: 2, label: "origin" };
const alsoAPoint: Point = coordinateWithLabel; // compiles - has x and y
console.log(`alsoAPoint = (${alsoAPoint.x}, ${alsoAPoint.y})`);

// ...but a fresh OBJECT LITERAL assigned directly (not through a variable)
// is checked more strictly ("excess property checking") specifically to
// catch typos in optional-looking fields:
// const strictCheck: Point = { x: 1, y: 2, lable: "typo" };
// tsc rejects: Object literal may only specify known properties, and
// 'lable' does not exist in type 'Point'.
// Going through the `coordinateWithLabel` variable above skips this extra
// check entirely - excess property checking applies only to literals
// written directly at the assignment site, which is a real, sharp edge of
// structural typing worth knowing, not a contradiction of it.

console.log(greetInterface({ id: 1, name: "Ana" }));
console.log(greetAlias({ id: 2, name: "Bilal" }));
