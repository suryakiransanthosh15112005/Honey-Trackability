import { useSelector, useDispatch } from 'react-redux'
import { loginWithPassword, sendOtp, verifyOtp, logout, clearError, resetOtpState } from '../authSlice'

export const useAuth = () => {
  const auth = useSelector((state) => state.auth)
  const dispatch = useDispatch()

  return {
    ...auth,
    loginWithPassword: (credentials) => dispatch(loginWithPassword(credentials)),
    sendOtp: (data) => dispatch(sendOtp(data)),
    verifyOtp: (data) => dispatch(verifyOtp(data)),
    logout: () => dispatch(logout()),
    clearError: () => dispatch(clearError()),
    resetOtpState: () => dispatch(resetOtpState()),
  }
}

export default useAuth
