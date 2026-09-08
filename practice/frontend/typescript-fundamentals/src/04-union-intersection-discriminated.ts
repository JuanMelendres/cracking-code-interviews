// Union types, intersection types, and discriminated unions.
//
// Verified by `tsc -p tsconfig.json` compiling this file with zero errors —
// see tsc-output-after-fix.txt (the whole-project run, this file included).
// The advanced use of this exact pattern for typed React component props
// is covered in react-typescript.md (F-119) - this file is its prerequisite.

// --- Union types: "this value is one of several types" -------------------

function formatId(id: string | number): string {
  // A union type only exposes operations valid on EVERY member - you must
  // narrow before using a type-specific method.
  if (typeof id === "number") {
    return id.toFixed(0); // safe - narrowed to `number` here
  }
  return id.toUpperCase(); // safe - narrowed to `string` here
}
console.log(formatId(42));
console.log(formatId("abc-123"));

// --- Intersection types: "this value is ALL of these types at once" ------

interface Timestamped {
  createdAt: Date;
}
interface Named {
  name: string;
}

// `TimestampedNamed` requires every field from BOTH `Timestamped` and
// `Named` - the value must have `createdAt` AND `name`.
type TimestampedNamed = Timestamped & Named;

const record: TimestampedNamed = {
  createdAt: new Date("2026-01-01"),
  name: "audit-log-entry",
};
console.log(`${record.name} @ ${record.createdAt.toISOString()}`);

// --- Discriminated unions: a union of object shapes sharing one literal --
// --- "discriminant" field that lets TypeScript narrow precisely ----------

type Shape =
  | { kind: "circle"; radius: number }
  | { kind: "rectangle"; width: number; height: number };

function area(shape: Shape): number {
  // Switching on `shape.kind` - the discriminant - narrows `shape` inside
  // each branch to EXACTLY the matching union member. Inside the "circle"
  // case, only `radius` exists; inside "rectangle", only `width`/`height`
  // exist. Accessing `shape.radius` inside the "rectangle" branch is a
  // real compile error, proven by the commented line below.
  switch (shape.kind) {
    case "circle":
      return Math.PI * shape.radius ** 2;
    case "rectangle":
      return shape.width * shape.height;
      // return shape.radius; // tsc rejects: Property 'radius' does not
      // exist on type '{ kind: "rectangle"; width: number; height: number }'.
  }
}
console.log(area({ kind: "circle", radius: 2 }).toFixed(2));
console.log(area({ kind: "rectangle", width: 3, height: 4 }));

// A plain union of PRIMITIVES has no discriminant to switch on - this is
// exactly why discriminated unions use a shared literal field on OBJECT
// members, not a bare union of object types with no common tag.
type Id = string | number;
function describeId(id: Id): string {
  return typeof id === "string" ? `string id: ${id}` : `numeric id: ${id}`;
}
console.log(describeId("abc"));
