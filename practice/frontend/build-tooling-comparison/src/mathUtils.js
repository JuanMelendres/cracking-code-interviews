// F-301: `add` is imported and used by App.jsx; `unusedSubtract` is
// exported but never imported anywhere in this app. Both reference a
// distinctive, real RUNTIME string (not a comment -- comments are
// stripped by minification regardless of tree-shaking, which is itself
// a real finding this chapter's evidence notes) so the built production
// output can be grepped afterward to prove which one survives.
export function add(a, b) {
  console.log("TREE_SHAKE_MARKER_ADD_KEPT");
  return a + b;
}

export function unusedSubtract(a, b) {
  console.log("TREE_SHAKE_MARKER_SUBTRACT_DEAD");
  return a - b;
}
