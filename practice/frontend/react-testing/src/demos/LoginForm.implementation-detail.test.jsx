import { fireEvent, render } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import LoginForm from './LoginForm';

// Implementation-detail: reaches into the DOM by structural position
// (the Nth ".input-group input") instead of asking "what would a user
// see." This chapter's README documents a real before/after test run:
// a pure markup refactor (renaming ".field-wrap" to ".input-group" AND
// reordering the two fields) broke this test with zero change in what
// a user can actually do, while LoginForm.behavior.test.jsx needed no
// changes at all. This version was updated to match the new markup —
// the maintenance tax that behavior-based queries don't pay.
describe('LoginForm (implementation-detail queries)', () => {
  it('submits the second field as username and first field as password', () => {
    const onSubmit = vi.fn();
    const { container } = render(<LoginForm onSubmit={onSubmit} />);

    const inputs = container.querySelectorAll('.input-group input');
    fireEvent.change(inputs[1], { target: { value: 'juan' } });
    fireEvent.change(inputs[0], { target: { value: 'hunter2' } });
    fireEvent.click(container.querySelector('button'));

    expect(onSubmit).toHaveBeenCalledWith({ username: 'juan', password: 'hunter2' });
  });
});
