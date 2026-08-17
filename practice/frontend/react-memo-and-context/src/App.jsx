import './App.css';
import ExpensiveMemoDemo from './demos/ExpensiveMemoDemo';
import MemoizedChildDemo from './demos/MemoizedChildDemo';
import PropDrillingVsContextDemo from './demos/PropDrillingVsContextDemo';
import ContextRerenderCostDemo from './demos/ContextRerenderCostDemo';

// Real demo app backing handbook/frontend/react-usememo-usecallback-and-usecontext.md
// (F-107: useMemo/useCallback, F-108: useContext). Verified live in a browser.
function App() {
  return (
    <div className="app-root">
      <h1>React Memoization &amp; Context — F-107 and F-108</h1>
      <ExpensiveMemoDemo />
      <MemoizedChildDemo />
      <PropDrillingVsContextDemo />
      <ContextRerenderCostDemo />
    </div>
  );
}

export default App;
