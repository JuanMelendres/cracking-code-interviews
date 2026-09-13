import { WindowWidthDisplayHOC } from './WithWindowWidthHOC';
import { WindowWidth } from './WindowWidthRenderProp';
import { useWindowWidth } from '../hooks/useWindowWidth';

// F-111a/b/c: the SAME behavior (live window width), implemented three
// ways. All three should show the identical width and all update together
// on a real resize -- proving functional equivalence. The difference
// between them is structural, visible directly in the code each requires:
//   - HOC: a whole extra class component (WithWindowWidth) is created and
//     mounted for you, injecting `width` as a prop into the wrapped
//     component -- two component instances involved, one hidden from the
//     call site.
//   - Render prop: one component (WindowWidth) owns the state and hands it
//     to a function you write inline -- no extra component definition
//     needed at the call site, but the JSX gets a function-as-children
//     level of nesting.
//   - Custom hook (useWindowWidth): zero extra components. The value is
//     just a variable in the SAME component that needs it.
function HookBasedDisplay() {
  const width = useWindowWidth();
  return <span data-testid="hook-width">Hook: window width = {width}px</span>;
}

export default function WindowWidthPatternsDemo() {
  return (
    <div className="demo-block">
      <h3>F-111a/b/c: three ways to share the same stateful behavior</h3>
      <p>Resize the browser window and confirm all three update together, to the identical value.</p>
      <div><WindowWidthDisplayHOC /></div>
      <div>
        <WindowWidth>
          {(width) => <span data-testid="render-prop-width">Render prop: window width = {width}px</span>}
        </WindowWidth>
      </div>
      <div><HookBasedDisplay /></div>
    </div>
  );
}
