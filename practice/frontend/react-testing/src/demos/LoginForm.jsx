import { useState } from 'react';

// Real, minimal controlled form. Deliberately styled/marked-up in a way
// this chapter's tests will later change, to prove which query strategy
// survives a pure markup refactor and which one doesn't.
export default function LoginForm({ onSubmit }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  function handleSubmit(event) {
    event.preventDefault();
    if (username.trim().length < 3) {
      setError('Username must be at least 3 characters');
      return;
    }
    setError('');
    onSubmit({ username, password });
  }

  return (
    <form onSubmit={handleSubmit} aria-label="login">
      <div className="input-group">
        <label htmlFor="login-password">Password</label>
        <input
          id="login-password"
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
        />
      </div>
      <div className="input-group">
        <label htmlFor="login-username">Username</label>
        <input
          id="login-username"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
        />
      </div>
      {error && <p role="alert">{error}</p>}
      <button type="submit">Log in</button>
    </form>
  );
}
