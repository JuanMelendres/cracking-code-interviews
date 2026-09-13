// F-301: only ever loaded via React.lazy()'s dynamic import() in App.jsx --
// exists specifically so the real production build's chunk manifest can
// be inspected for a SEPARATE file this component's own code lives in,
// not bundled into the main entry chunk.
export default function LazyPanel() {
  return <p data-testid="lazy-panel">LAZY_CHUNK_MARKER_LOADED_ON_DEMAND</p>;
}
