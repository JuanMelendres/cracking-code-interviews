// Module-level state, shared across every request handled by THIS server
// process. That sharing is real (proven live in the F-207 demo: a POST
// changes what a later GET sees) but it is a property of running under
// `next start` as a single long-lived Node process, not a Route Handler
// guarantee — the framework's own docs call out that on a lambda-style
// host, each request can land on a different instance with its own copy
// of this array, so this pattern would silently stop working there.
let widgets = [
  { id: "1", name: "Wrench", qty: 12 },
  { id: "2", name: "Hammer", qty: 5 },
];
let nextId = 3;

export function getAll() {
  return widgets;
}

export function getById(id) {
  return widgets.find((w) => w.id === id);
}

export function add({ name, qty }) {
  const widget = { id: String(nextId++), name, qty };
  widgets.push(widget);
  return widget;
}

export function update(id, patch) {
  const widget = getById(id);
  if (!widget) return null;
  Object.assign(widget, patch);
  return widget;
}

export function remove(id) {
  const before = widgets.length;
  widgets = widgets.filter((w) => w.id !== id);
  return widgets.length < before;
}
