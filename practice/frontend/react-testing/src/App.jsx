import { useState } from 'react';
import './App.css';
import LoginForm from './demos/LoginForm';
import UserProfile from './demos/UserProfile';

// Real demo app backing handbook/frontend/react-testing.md (F-118).
// The tests themselves are the real evidence for this chapter — see
// LoginForm.behavior.test.jsx / LoginForm.implementation-detail.test.jsx
// and UserProfile.test.jsx, plus the E2E flow under e2e/.
function App() {
  const [submitted, setSubmitted] = useState(null);

  return (
    <div className="app-root">
      <h1>React Testing — F-118</h1>

      <section>
        <h2>Login form (tested two ways)</h2>
        <LoginForm onSubmit={setSubmitted} />
        {submitted && (
          <p data-testid="submitted-payload">
            Submitted: {submitted.username} / {submitted.password}
          </p>
        )}
      </section>

      <section>
        <h2>User profile (mocked async dependency in tests)</h2>
        <UserProfile userId={7} />
      </section>
    </div>
  );
}

export default App;
