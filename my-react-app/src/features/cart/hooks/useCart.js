import { useEffect, useCallback } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import {
  fetchCart,
  addToCart,
  updateCartItem,
  removeCartItem,
  clearCart,
} from '../cartSlice'

export const useCart = () => {
  const dispatch = useDispatch()
  const { cartId, items, subtotal, itemCount, loading, error } = useSelector(
    (state) => state.cart
  )
  const { isAuthenticated, role } = useSelector((state) => state.auth)

  useEffect(() => {
    if (isAuthenticated && role === 'CUSTOMER') {
      dispatch(fetchCart())
    }
  }, [dispatch, isAuthenticated, role])

  const addItem = useCallback(
    (productId, quantityKg) => dispatch(addToCart({ productId, quantityKg })),
    [dispatch]
  )

  const updateQuantity = useCallback(
    (id, quantityKg) => dispatch(updateCartItem({ id, data: { quantityKg } })),
    [dispatch]
  )

  const removeItem = useCallback(
    (id) => dispatch(removeCartItem(id)),
    [dispatch]
  )

  const emptyCart = useCallback(
    () => dispatch(clearCart()),
    [dispatch]
  )

  const reload = useCallback(
    () => dispatch(fetchCart()),
    [dispatch]
  )

  return {
    cartId,
    items,
    subtotal,
    itemCount,
    loading,
    error,
    addItem,
    updateQuantity,
    removeItem,
    emptyCart,
    reload,
  }
}

export default useCart
