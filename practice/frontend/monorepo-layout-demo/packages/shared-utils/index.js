// F-303: a real, shared package consumed by BOTH web-app (frontend-flavored)
// and api-service (backend-flavored) via the npm workspace protocol -- no
// publishing to a registry, no version pinning, no copy-paste.
export function formatGreeting(name) {
  return `SHARED_UTILS_V1: Hello, ${name}!`;
}
