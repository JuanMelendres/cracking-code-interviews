// Deliberately framework-free: a plain function that mounts a DOM node.
// This is the "remote" micro-frontend's one exposed module -- a real,
// independently owned and independently deployable piece of UI.
export default function mountButton(container) {
  const el = document.createElement('button');
  el.id = 'remote-button';
  el.textContent = 'Remote Button v1';
  el.style.background = '#2b6cb0';
  el.style.color = 'white';
  el.style.padding = '8px 16px';
  container.appendChild(el);
  return el;
}
