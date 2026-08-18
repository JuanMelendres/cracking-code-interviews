"use client";

import { useActionState } from "react";
import { login } from "../actions/auth";

export default function LoginForm() {
  const [state, action, pending] = useActionState(login, undefined);

  return (
    <form action={action}>
      <div>
        <label htmlFor="username">Username</label>
        <input id="username" name="username" defaultValue="demo" />
      </div>
      <div>
        <label htmlFor="password">Password</label>
        <input id="password" name="password" type="password" defaultValue="correct-horse" />
      </div>
      {state?.error && <p data-testid="login-error">{state.error}</p>}
      <button disabled={pending} type="submit">
        {pending ? "Signing in..." : "Sign in"}
      </button>
    </form>
  );
}
