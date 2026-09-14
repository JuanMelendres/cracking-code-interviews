// F-301: the SAME real tree-shaking test as build-tooling-comparison's own
// src/mathUtils.js -- `add` is imported and used, `unusedSubtract` is
// exported but never imported anywhere in this app. Markers are real
// runtime console.log strings, not comments (comments are stripped by
// minification regardless of tree-shaking).
export function add(a, b) {
  console.log("TREE_SHAKE_MARKER_ADD_KEPT");
  return a + b;
}

export function unusedSubtract(a, b) {
  console.log("TREE_SHAKE_MARKER_SUBTRACT_DEAD");
  return a - b;
}
