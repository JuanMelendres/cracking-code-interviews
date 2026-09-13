import { render, screen } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import UserProfile from './UserProfile';
import { fetchUser } from '../api/fetchUser';

// vi.mock replaces the whole module: no real fetch() ever happens.
// This suite verifies both the RETURN VALUE behavior (loading -> name)
// and the INTERACTION (fetchUser called with the right id) — the
// second is something a stub-only return-value check can't prove.
vi.mock('../api/fetchUser', () => ({
  fetchUser: vi.fn(),
}));

describe('UserProfile (mocked dependency)', () => {
  beforeEach(() => {
    vi.mocked(fetchUser).mockReset();
  });

  it('shows a loading state, then the user name once fetchUser resolves', async () => {
    vi.mocked(fetchUser).mockResolvedValue({ id: 7, name: 'Ada Lovelace' });

    render(<UserProfile userId={7} />);

    expect(screen.getByText('Loading user...')).toBeInTheDocument();
    expect(await screen.findByText('Ada Lovelace')).toBeInTheDocument();
  });

  it('calls fetchUser with the exact id it was given', async () => {
    vi.mocked(fetchUser).mockResolvedValue({ id: 42, name: 'Grace Hopper' });

    render(<UserProfile userId={42} />);
    await screen.findByText('Grace Hopper');

    expect(fetchUser).toHaveBeenCalledTimes(1);
    expect(fetchUser).toHaveBeenCalledWith(42);
  });

  it('shows an error message when fetchUser rejects', async () => {
    vi.mocked(fetchUser).mockRejectedValue(new Error('network down'));

    render(<UserProfile userId={7} />);

    expect(await screen.findByRole('alert')).toHaveTextContent('Could not load user');
  });
});
