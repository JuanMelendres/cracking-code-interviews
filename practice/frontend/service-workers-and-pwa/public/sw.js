const CACHE_NAME = 'demo-cache-v1';

self.addEventListener('install', (event) => {
  // Real cache-first asset, populated at install time -- a real Cache
  // Storage entry, not an in-memory stand-in.
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(['/app.css']))
  );
  self.skipWaiting();
});

self.addEventListener('activate', (event) => {
  event.waitUntil(self.clients.claim());
});

self.addEventListener('fetch', (event) => {
  const url = new URL(event.request.url);

  if (url.pathname === '/app.css') {
    // Cache-first: static assets rarely change: serve from cache
    // immediately if present, only hit the network on a real cache miss.
    event.respondWith(
      caches.match(event.request).then((cached) => {
        if (cached) return cached;
        return fetch(event.request).then((networkResponse) => {
          return caches.open(CACHE_NAME).then((cache) => {
            cache.put(event.request, networkResponse.clone());
            return networkResponse;
          });
        });
      })
    );
    return;
  }

  if (url.pathname === '/api/data') {
    // Network-first: data should be as fresh as possible, but the app
    // should still work offline using the last real successful response.
    event.respondWith(
      fetch(event.request)
        .then((networkResponse) => {
          return caches.open(CACHE_NAME).then((cache) => {
            cache.put(event.request, networkResponse.clone());
            return networkResponse;
          });
        })
        .catch(() => caches.match(event.request))
    );
    return;
  }

  // Everything else: pass through untouched, no caching strategy applied.
});
