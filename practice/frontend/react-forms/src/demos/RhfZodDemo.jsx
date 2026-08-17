import { useRef } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const schema = z.object({
  username: z.string().min(3, 'Username must be at least 3 characters'),
  email: z.string().email('Enter a valid email address'),
  age: z.coerce.number().int().min(18, 'Must be 18 or older'),
});

// Real react-hook-form + zod integration, not a hand-rolled approximation.
// RHF registers uncontrolled inputs (via `register`, internally refs, the
// same mechanism as ControlledVsUncontrolledDemo's uncontrolled field) so
// typing does NOT re-render this component on every keystroke — only a
// validation-relevant re-render (e.g. showing/clearing an error) does.
export default function RhfZodDemo() {
  const renderCount = useRef(0);
  renderCount.current += 1;

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitSuccessful, submitCount },
  } = useForm({
    resolver: zodResolver(schema),
    mode: 'onSubmit',
  });

  function onValid(data) {
    // Real submit handler; only called when zod validation passes.
    // eslint-disable-next-line no-console
    console.log('RHF valid submit', data);
  }

  return (
    <section className="demo">
      <h2>3. React Hook Form + Zod — real schema validation</h2>
      <form onSubmit={handleSubmit(onValid)}>
        <div className="field">
          <label>
            Username
            <input data-testid="rhf-username" {...register('username')} />
          </label>
          <p data-testid="rhf-username-error">{errors.username?.message || '(no error)'}</p>
        </div>
        <div className="field">
          <label>
            Email
            <input data-testid="rhf-email" {...register('email')} />
          </label>
          <p data-testid="rhf-email-error">{errors.email?.message || '(no error)'}</p>
        </div>
        <div className="field">
          <label>
            Age
            <input data-testid="rhf-age" {...register('age')} />
          </label>
          <p data-testid="rhf-age-error">{errors.age?.message || '(no error)'}</p>
        </div>
        <button type="submit">Submit</button>
      </form>
      <p data-testid="rhf-render-count">renders: {renderCount.current}</p>
      <p data-testid="rhf-submit-count">submit attempts: {submitCount}</p>
      <p data-testid="rhf-success">submitted successfully: {String(isSubmitSuccessful)}</p>
    </section>
  );
}
