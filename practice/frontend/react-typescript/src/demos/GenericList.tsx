import type { ReactNode } from 'react';

// A generic component: <T,> is the generic parameter (the trailing comma
// disambiguates it from JSX in a .tsx file). ListProps<T> ties three
// things together generically: the array of items, a render function that
// receives a T, and a key-extraction function that also receives a T —
// the compiler enforces all three agree on the same T at each call site.
interface ListProps<T> {
  items: T[];
  renderItem: (item: T) => ReactNode;
  keyExtractor: (item: T) => string | number;
}

export default function List<T>({ items, renderItem, keyExtractor }: ListProps<T>) {
  return (
    <ul>
      {items.map((item) => (
        <li key={keyExtractor(item)}>{renderItem(item)}</li>
      ))}
    </ul>
  );
}

interface Task {
  id: number;
  label: string;
  done: boolean;
}

const tasks: Task[] = [
  { id: 1, label: 'Write generic component demo', done: true },
  { id: 2, label: 'Prove a real tsc error', done: false },
];

const scores: number[] = [91, 87, 74];

export function GenericListDemo() {
  return (
    <div>
      <h4>List&lt;Task&gt; — T inferred as Task</h4>
      <List
        items={tasks}
        keyExtractor={(task) => task.id}
        renderItem={(task) => `${task.done ? '[x]' : '[ ]'} ${task.label}`}
      />

      <h4>List&lt;number&gt; — the same component, T inferred as number</h4>
      <List items={scores} keyExtractor={(score) => score} renderItem={(score) => `Score: ${score}`} />
    </div>
  );
}
