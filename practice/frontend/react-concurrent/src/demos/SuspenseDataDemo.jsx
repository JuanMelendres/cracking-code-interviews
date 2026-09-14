import { Suspense, use, useState } from 'react';

// Simulated network call. Real delay via setTimeout, not faked/instant —
// the Suspense fallback must actually be visible for a measurable window.
function fetchUser(id) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({ id, name: `User ${id}`, loadedAt: Date.now() });
    }, 3000);
  });
}

function UserCard({ resource }) {
  const user = use(resource); // suspends the component, not the whole tree, until resolved
  return (
    <p data-testid="suspense-loaded">
      Loaded: {user.name} (id {user.id})
    </p>
  );
}

export default function SuspenseDataDemo() {
  const [resource, setResource] = useState(null);
  const [clickCount, setClickCount] = useState(0);

  function handleLoad() {
    setClickCount((c) => c + 1);
    setResource(fetchUser(clickCount + 1)); // new promise each click, no caching
  }

  return (
    <section className="demo">
      <h2>3. Suspense + use() — data fetching that suspends</h2>
      <button onClick={handleLoad}>Load user</button>
      <p data-testid="suspense-click-count">clicks: {clickCount}</p>
      {resource ? (
        <Suspense fallback={<p data-testid="suspense-fallback">Loading user...</p>}>
          <UserCard resource={resource} />
        </Suspense>
      ) : (
        <p data-testid="suspense-idle">idle: click "Load user" to fetch</p>
      )}
    </section>
  );
}
