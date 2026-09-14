import { create } from 'zustand';

// No Provider, no slices, no action creators — one create() call.
// Components subscribe via a selector function passed directly to the
// hook, and zustand only re-renders a component when ITS selector's
// return value actually changes, same guarantee as Redux's useSelector
// but with a fraction of the setup code.
export const useAppStore = create((set) => ({
  count: 0,
  name: 'anon',
  increment: () => set((state) => ({ count: state.count + 1 })),
  setName: (name) => set({ name }),
}));
