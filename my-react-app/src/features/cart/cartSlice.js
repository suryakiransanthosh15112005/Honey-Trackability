import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import cartApi from './api/cartApi'

export const fetchCart = createAsyncThunk('cart/fetchCart', async (_, { rejectWithValue }) => {
  try {
    const res = await cartApi.getCart()
    return res.data?.data || { items: [], subtotal: 0, itemCount: 0 }
  } catch (err) {
    return rejectWithValue(err?.response?.data?.message || 'Failed to fetch cart')
  }
})

export const addToCart = createAsyncThunk('cart/addToCart', async (payload, { rejectWithValue }) => {
  try {
    const res = await cartApi.addItem(payload)
    return res.data?.data || { items: [], subtotal: 0, itemCount: 0 }
  } catch (err) {
    return rejectWithValue(err?.response?.data?.message || 'Failed to add item to cart')
  }
})

export const updateCartItem = createAsyncThunk('cart/updateCartItem', async ({ id, data }, { rejectWithValue }) => {
  try {
    const res = await cartApi.updateItem(id, data)
    return res.data?.data || { items: [], subtotal: 0, itemCount: 0 }
  } catch (err) {
    return rejectWithValue(err?.response?.data?.message || 'Failed to update cart item')
  }
})

export const removeCartItem = createAsyncThunk('cart/removeCartItem', async (id, { rejectWithValue }) => {
  try {
    const res = await cartApi.removeItem(id)
    return res.data?.data || { items: [], subtotal: 0, itemCount: 0 }
  } catch (err) {
    return rejectWithValue(err?.response?.data?.message || 'Failed to remove cart item')
  }
})

export const clearCart = createAsyncThunk('cart/clearCart', async (_, { rejectWithValue }) => {
  try {
    const res = await cartApi.clearCart()
    return res.data?.data || { items: [], subtotal: 0, itemCount: 0 }
  } catch (err) {
    return rejectWithValue(err?.response?.data?.message || 'Failed to clear cart')
  }
})

const initialState = {
  cartId: null,
  items: [],
  subtotal: 0,
  itemCount: 0,
  loading: false,
  error: null,
}

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    resetCartState: (state) => {
      state.cartId = null
      state.items = []
      state.subtotal = 0
      state.itemCount = 0
      state.loading = false
      state.error = null
    },
  },
  extraReducers: (builder) => {
    const handlePending = (state) => {
      state.loading = true
      state.error = null
    }
    const handleFulfilled = (state, action) => {
      state.loading = false
      state.cartId = action.payload?.cartId || null
      state.items = action.payload?.items || []
      state.subtotal = action.payload?.subtotal || 0
      state.itemCount = action.payload?.itemCount || (action.payload?.items?.length || 0)
    }
    const handleRejected = (state, action) => {
      state.loading = false
      state.error = action.payload || 'Cart operation failed'
    }

    builder
      .addCase(fetchCart.pending, handlePending)
      .addCase(fetchCart.fulfilled, handleFulfilled)
      .addCase(fetchCart.rejected, handleRejected)
      .addCase(addToCart.pending, handlePending)
      .addCase(addToCart.fulfilled, handleFulfilled)
      .addCase(addToCart.rejected, handleRejected)
      .addCase(updateCartItem.pending, handlePending)
      .addCase(updateCartItem.fulfilled, handleFulfilled)
      .addCase(updateCartItem.rejected, handleRejected)
      .addCase(removeCartItem.pending, handlePending)
      .addCase(removeCartItem.fulfilled, handleFulfilled)
      .addCase(removeCartItem.rejected, handleRejected)
      .addCase(clearCart.pending, handlePending)
      .addCase(clearCart.fulfilled, handleFulfilled)
      .addCase(clearCart.rejected, handleRejected)
  },
})

export const { resetCartState } = cartSlice.actions
export default cartSlice.reducer
