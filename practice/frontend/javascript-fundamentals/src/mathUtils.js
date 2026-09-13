// A tiny module used by moduleDemo.js to demonstrate real ESM import/export.

export const PI_APPROX = 3.14159;

export function square(n) {
  return n * n;
}

export function circleArea(radius) {
  return PI_APPROX * square(radius);
}

export default function describeNumber(n) {
  return `${n} squared is ${square(n)}`;
}
