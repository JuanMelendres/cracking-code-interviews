"use client";

// F-301: only ever loaded via next/dynamic's dynamic import() in
// F301Demo.js -- exists so the real production build's chunk manifest can
// be inspected for a SEPARATE file this component's own code lives in.
export default function F301LazyPanel() {
  return <p data-testid="f301-lazy-panel">LAZY_CHUNK_MARKER_LOADED_ON_DEMAND</p>;
}
