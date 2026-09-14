"use client";

import { useEffect, useState } from "react";

export default function WidgetsClient() {
  const [widgets, setWidgets] = useState(null);
  const [cachedCount, setCachedCount] = useState(null);
  const [name, setName] = useState("");
  const [qty, setQty] = useState("");
  const [formError, setFormError] = useState(null);
  const [proxyResult, setProxyResult] = useState(null);

  async function loadWidgets() {
    const res = await fetch("/api/widgets");
    setWidgets(await res.json());
  }

  async function loadCachedCount() {
    const res = await fetch("/api/widgets/cached-count");
    setCachedCount(await res.json());
  }

  useEffect(() => {
    loadWidgets();
    loadCachedCount();
  }, []);

  async function handleAdd(event) {
    event.preventDefault();
    setFormError(null);
    const res = await fetch("/api/widgets", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, qty: Number(qty) }),
    });
    if (!res.ok) {
      const body = await res.json();
      setFormError(`${res.status}: ${body.error}`);
      return;
    }
    setName("");
    setQty("");
    await loadWidgets();
  }

  async function handleDelete(id) {
    await fetch(`/api/widgets/${id}`, { method: "DELETE" });
    await loadWidgets();
  }

  async function handleProxyCall() {
    const res = await fetch("/api/uuid-proxy");
    setProxyResult(await res.json());
  }

  if (!widgets) return <p>Loading…</p>;

  return (
    <div>
      <section>
        <h2>Widgets (live, default GET -- not cached)</h2>
        <ul data-testid="widget-list">
          {widgets.map((w) => (
            <li key={w.id} data-testid="widget-row">
              {w.name} (qty: {w.qty})
              <button type="button" onClick={() => handleDelete(w.id)}>
                Delete
              </button>
            </li>
          ))}
        </ul>
        <form onSubmit={handleAdd}>
          <input
            aria-label="widget name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="name"
          />
          <input
            aria-label="widget qty"
            value={qty}
            onChange={(e) => setQty(e.target.value)}
            placeholder="qty"
          />
          <button type="submit">Add widget</button>
        </form>
        {formError && <p data-testid="form-error">{formError}</p>}
      </section>

      <section>
        <h2>Cached count (force-static, frozen at build time)</h2>
        <p data-testid="cached-count">
          {cachedCount ? `${cachedCount.count} (${cachedCount.note})` : "loading…"}
        </p>
        <p>Live count right now: {widgets.length}</p>
        <button type="button" onClick={loadCachedCount}>
          Re-fetch cached-count
        </button>
      </section>

      <section>
        <h2>BFF proxy (server-side call to httpbin, reshaped)</h2>
        <button type="button" onClick={handleProxyCall}>
          Call /api/uuid-proxy
        </button>
        {proxyResult && <pre data-testid="proxy-result">{JSON.stringify(proxyResult, null, 2)}</pre>}
      </section>
    </div>
  );
}
