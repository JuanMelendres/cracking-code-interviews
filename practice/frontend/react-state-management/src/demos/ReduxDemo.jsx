import { useRef } from 'react';
import { Provider, useDispatch, useSelector } from 'react-redux';
import { reduxStore, increment, setName } from '../store/reduxStore';

// Selects ONLY state.counter.value. useSelector re-renders this
// component only when the SELECTED value changes (by reference/===
// by default) — updating state.name never touches state.counter, so
// this component's selector keeps returning the same value.
function CounterView() {
  const count = useSelector((state) => state.counter.value);
  const dispatch = useDispatch();
  const renderCount = useRef(0);
  renderCount.current += 1;
  return (
    <p data-testid="redux-counter">
      Count: {count} (renders: {renderCount.current})
      <button type="button" onClick={() => dispatch(increment())}>
        +1 count
      </button>
    </p>
  );
}

// Selects ONLY state.name.value — never re-renders when counter
// changes, unlike ContextDemo's equivalent consumer.
function NameView() {
  const name = useSelector((state) => state.name.value);
  const dispatch = useDispatch();
  const renderCount = useRef(0);
  renderCount.current += 1;
  return (
    <p data-testid="redux-name">
      Name: {name} (renders: {renderCount.current})
      <button type="button" onClick={() => dispatch(setName('ada'))}>
        set name
      </button>
    </p>
  );
}

export default function ReduxDemo() {
  return (
    <Provider store={reduxStore}>
      <CounterView />
      <NameView />
    </Provider>
  );
}
