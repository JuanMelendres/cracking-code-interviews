"use server";

import { redirect } from "next/navigation";
import { createSession, deleteSession } from "../../lib/session";

// A hardcoded demo credential -- this app has no real user database.
// A real app calls a real authentication provider or DB here instead.
const DEMO_USER = { id: "user-42", username: "demo", password: "correct-horse" };

export async function login(prevState, formData) {
  const username = formData.get("username");
  const password = formData.get("password");

  if (username !== DEMO_USER.username || password !== DEMO_USER.password) {
    return { error: "Invalid username or password." };
  }

  await createSession(DEMO_USER.id);
  redirect("/dashboard");
}

export async function logout() {
  await deleteSession();
  redirect("/");
}
