// Real fetch wrapper. In the app it hits a real endpoint; in tests it's
// mocked with vi.fn()/vi.mock so UserProfile.test.jsx never makes a
// real network call.
export async function fetchUser(id) {
  const response = await fetch(`/api/users/${id}`);
  if (!response.ok) {
    throw new Error(`Failed to load user ${id}`);
  }
  return response.json();
}
