import { QueryClient, QueryClientProvider, useQuery } from '@tanstack/react-query';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // 60s staleTime: a remount within this window reuses the cached
      // value with zero network request, instead of the default
      // "refetch on every mount" behavior.
      staleTime: 60_000,
      retry: false,
    },
  },
});

async function fetchTodo() {
  const response = await fetch('/api/todos/1');
  if (!response.ok) {
    throw new Error(`Failed to load todo: ${response.status}`);
  }
  return response.json();
}

// Two independent components, same queryKey. This is server state —
// data owned by a remote source, not by any single component — and
// that's exactly the distinction from client state (count, form input)
// that Context/Redux/Zustand were built for.
function TodoViewer({ label }) {
  const { data, isLoading, isError } = useQuery({ queryKey: ['todo', 1], queryFn: fetchTodo });
  return (
    <p data-testid={`query-viewer-${label}`}>
      {label}: {isLoading ? 'loading...' : isError ? 'error (expected — no real backend here)' : JSON.stringify(data)}
    </p>
  );
}

export default function QueryDemo() {
  return (
    <QueryClientProvider client={queryClient}>
      <TodoViewer label="a" />
      <TodoViewer label="b" />
    </QueryClientProvider>
  );
}
