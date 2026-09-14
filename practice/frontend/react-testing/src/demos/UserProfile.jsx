import { useEffect, useState } from 'react';
import { fetchUser } from '../api/fetchUser';

// Real async component: loading -> loaded/error, driven by a real
// dependency (fetchUser) that tests mock instead of hitting a network.
export default function UserProfile({ userId }) {
  const [status, setStatus] = useState('loading');
  const [user, setUser] = useState(null);

  useEffect(() => {
    let cancelled = false;
    setStatus('loading');
    fetchUser(userId)
      .then((data) => {
        if (cancelled) return;
        setUser(data);
        setStatus('loaded');
      })
      .catch(() => {
        if (cancelled) return;
        setStatus('error');
      });
    return () => {
      cancelled = true;
    };
  }, [userId]);

  if (status === 'loading') return <p>Loading user...</p>;
  if (status === 'error') return <p role="alert">Could not load user</p>;
  return <p>{user.name}</p>;
}
