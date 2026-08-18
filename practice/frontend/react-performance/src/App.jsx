import './App.css';
import VirtualizedListDemo from './demos/VirtualizedListDemo';
import MemoizationStrategyDemo from './demos/MemoizationStrategyDemo';
import CodeSplittingDemo from './demos/CodeSplittingDemo';

// Real demo app backing handbook/frontend/react-performance.md (F-117).
// Verified live in a browser, including a real DOM-node-count contrast,
// a real memo()-fails-silently reproduction, and a real network-request
// trace proving a lazy-loaded chunk is fetched on demand.
function App() {
  return (
    <div className="app-root">
      <h1>React Performance — F-117</h1>
      <VirtualizedListDemo />
      <MemoizationStrategyDemo />
      <CodeSplittingDemo />
    </div>
  );
}

export default App;
