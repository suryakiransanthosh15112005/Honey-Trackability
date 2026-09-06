import { useDispatch, useSelector } from 'react-redux'

// Typed hooks for use throughout the app
export const useAppDispatch = () => useDispatch()
export const useAppSelector = (selector) => useSelector(selector)

// Auth selectors
export const useAuth = () => useAppSelector((state) => state.auth)
