import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import LoginForm from './LoginForm';

// Behavior-based: queries by role/label, the same way a real user
// (or a screen reader) finds these elements. Survives markup refactors
// that don't change what the user actually sees or does.
describe('LoginForm (behavior-based queries)', () => {
  it('submits the entered username and password', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn();
    render(<LoginForm onSubmit={onSubmit} />);

    await user.type(screen.getByLabelText('Username'), 'juan');
    await user.type(screen.getByLabelText('Password'), 'hunter2');
    await user.click(screen.getByRole('button', { name: 'Log in' }));

    expect(onSubmit).toHaveBeenCalledTimes(1);
    expect(onSubmit).toHaveBeenCalledWith({ username: 'juan', password: 'hunter2' });
  });

  it('shows a validation error and does not submit for a too-short username', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn();
    render(<LoginForm onSubmit={onSubmit} />);

    await user.type(screen.getByLabelText('Username'), 'ab');
    await user.click(screen.getByRole('button', { name: 'Log in' }));

    expect(screen.getByRole('alert')).toHaveTextContent('Username must be at least 3 characters');
    expect(onSubmit).not.toHaveBeenCalled();
  });
});
