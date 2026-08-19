// F-213: an INLINE Server Action, defined inside this Server Component's own
// function body so it genuinely closes over `instanceLabel` -- a real,
// different case from app/notes/actions.js's top-level exported actions,
// which capture nothing from an enclosing scope. This exists specifically
// to test whether the framework's own documented NEXT_SERVER_ACTIONS_
// ENCRYPTION_KEY requirement applies to plain top-level actions too, or
// only to actions that actually close over a value like this one does.
export default async function DeployTestPage() {
  const instanceLabel = `built-at-${process.env.NEXT_BUILD_LABEL ?? "unset"}`;

  async function echoClosure(formData) {
    "use server";
    return { echoed: instanceLabel };
  }

  return (
    <main>
      <h1>Deploy Test (F-213): inline closure Server Action</h1>
      <p data-testid="instance-label">{instanceLabel}</p>
      <form action={echoClosure}>
        <button type="submit">Echo closure</button>
      </form>
    </main>
  );
}
