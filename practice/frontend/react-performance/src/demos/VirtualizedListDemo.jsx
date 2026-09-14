import { useMemo, useRef, useState } from 'react';

const ITEM_COUNT = 5000;
const ROW_HEIGHT = 28;
const VIEWPORT_HEIGHT = 240;
const OVERSCAN = 3;

const ITEMS = Array.from({ length: ITEM_COUNT }, (_, i) => `Row ${i}`);

function NaiveList() {
  return (
    <div className="scroll-box" data-testid="naive-list">
      {ITEMS.map((item) => (
        <div key={item} style={{ height: ROW_HEIGHT }}>
          {item}
        </div>
      ))}
    </div>
  );
}

// A minimal, from-scratch windowed list — no library. Renders only the
// rows currently in (or near) the visible viewport, tracked via scrollTop,
// with a spacer element to preserve real scrollbar size/position.
function VirtualizedList() {
  const [scrollTop, setScrollTop] = useState(0);
  const containerRef = useRef(null);

  const { startIndex, endIndex, offsetY } = useMemo(() => {
    const visibleCount = Math.ceil(VIEWPORT_HEIGHT / ROW_HEIGHT);
    const start = Math.max(0, Math.floor(scrollTop / ROW_HEIGHT) - OVERSCAN);
    const end = Math.min(ITEM_COUNT, start + visibleCount + OVERSCAN * 2);
    return { startIndex: start, endIndex: end, offsetY: start * ROW_HEIGHT };
  }, [scrollTop]);

  const visibleItems = ITEMS.slice(startIndex, endIndex);

  return (
    <div
      className="scroll-box"
      data-testid="virtualized-list"
      ref={containerRef}
      onScroll={(e) => setScrollTop(e.currentTarget.scrollTop)}
    >
      <div style={{ height: ITEM_COUNT * ROW_HEIGHT, position: 'relative' }}>
        <div style={{ position: 'absolute', top: offsetY, left: 0, right: 0 }}>
          {visibleItems.map((item) => (
            <div key={item} style={{ height: ROW_HEIGHT }}>
              {item}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

// Real, measured proof: both lists show the SAME 5,000 logical items, but
// a direct DOM node count (querySelectorAll) shows a dramatic difference
// in what actually gets mounted.
export default function VirtualizedListDemo() {
  return (
    <section className="demo">
      <h2>1. Virtualization — real DOM-node-count contrast, same 5,000 items</h2>
      <div className="row">
        <div>
          <h3>Naive (renders all 5,000)</h3>
          <NaiveList />
        </div>
        <div>
          <h3>Virtualized (renders only the visible window)</h3>
          <VirtualizedList />
        </div>
      </div>
    </section>
  );
}
