import { configureStore, createSlice } from '@reduxjs/toolkit';

// Two independent reducer slices, combined by configureStore into one
// tree: state.counter and state.name. Each slice's reducer only ever
// touches its own branch, so updating one never changes the other
// branch's object reference.
const counterSlice = createSlice({
  name: 'counter',
  initialState: { value: 0 },
  reducers: {
    increment: (state) => {
      state.value += 1;
    },
  },
});

const nameSlice = createSlice({
  name: 'name',
  initialState: { value: 'anon' },
  reducers: {
    setName: (state, action) => {
      state.value = action.payload;
    },
  },
});

export const { increment } = counterSlice.actions;
export const { setName } = nameSlice.actions;

export const reduxStore = configureStore({
  reducer: {
    counter: counterSlice.reducer,
    name: nameSlice.reducer,
  },
});
