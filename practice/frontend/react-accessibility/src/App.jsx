import './App.css';
import SemanticVsDivButtonDemo from './demos/SemanticVsDivButtonDemo';
import FocusTrapModalDemo from './demos/FocusTrapModalDemo';
import AccessibleFormErrorDemo from './demos/AccessibleFormErrorDemo';

// Real demo app backing handbook/frontend/react-accessibility.md (F-116).
// Verified live in a browser, including real keyboard-only navigation
// checks and real document.activeElement focus tracking.
function App() {
  return (
    <div className="app-root">
      <h1>React Accessibility — F-116</h1>
      <SemanticVsDivButtonDemo />
      <FocusTrapModalDemo />
      <AccessibleFormErrorDemo />
    </div>
  );
}

export default App;
