// A discriminated union for variant props: the `variant` field is the
// discriminant. TypeScript narrows AlertProps to the specific member
// based on the literal value of `variant`, so `onRetry` is only visible
// (and only required) on the 'error' branch — not an optional field
// that happens to be used conditionally, but a type-level guarantee
// that an 'error' alert without onRetry cannot compile at all.
type AlertProps =
  | { variant: 'info'; message: string }
  | { variant: 'success'; message: string }
  | { variant: 'error'; message: string; onRetry: () => void };

export default function Alert(props: AlertProps) {
  if (props.variant === 'error') {
    // Inside this branch, TypeScript has narrowed `props` to the
    // 'error' member specifically — props.onRetry is valid here and
    // would NOT be valid in the 'info'/'success' branches.
    return (
      <div className={`alert alert-${props.variant}`} role="alert">
        <p>{props.message}</p>
        <button type="button" onClick={props.onRetry}>
          Retry
        </button>
      </div>
    );
  }

  return (
    <div className={`alert alert-${props.variant}`} role="status">
      <p>{props.message}</p>
    </div>
  );
}
