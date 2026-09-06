import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import Input from '../../../components/ui/Input'
import Button from '../../../components/ui/Button'
import Alert from '../../../components/feedback/Alert'
import { ROLES } from '../../../constants/roles'

export const RegisterPage = () => {
  const [formData, setFormData] = useState({
    phoneNumber: '',
    role: ROLES.BEEKEEPER,
  })

  const { sendOtp, loading, error, clearError } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    const result = await sendOtp(formData)
    if (!result.error) {
      navigate('/otp-login')
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-page__orb auth-page__orb--tl" />

      <div className="auth-card">
        <div className="auth-header">
          <Link to="/" className="inline-flex flex-col items-center gap-2">
            <div className="auth-logo-icon">🍯</div>
            <span className="text-gradient font-black text-xl font-brand">HoneyChain</span>
          </Link>
          <h1 className="auth-header__title">Create Account</h1>
          <p className="auth-header__sub">Register as a Beekeeper or Customer</p>
        </div>

        {error && <Alert type="error" message={error} onClose={clearError} className="mb-4" />}

        <form id="register-form" onSubmit={handleSubmit} className="flex-col gap-4">
          <div>
            <label className="form-label mb-2">I want to register as...</label>
            <div className="role-grid">
              <button
                type="button"
                onClick={() => setFormData({ ...formData, role: ROLES.BEEKEEPER })}
                className={`role-btn${formData.role === ROLES.BEEKEEPER ? ' role-btn--active' : ''}`}
              >
                🌿 Beekeeper
              </button>
              <button
                type="button"
                onClick={() => setFormData({ ...formData, role: ROLES.CUSTOMER })}
                className={`role-btn${formData.role === ROLES.CUSTOMER ? ' role-btn--active' : ''}`}
              >
                🛒 Customer
              </button>
            </div>
          </div>

          <Input
            id="register-phone"
            name="phoneNumber"
            label="Phone Number"
            value={formData.phoneNumber}
            onChange={(e) => {
              setFormData({ ...formData, phoneNumber: e.target.value })
              if (error) clearError()
            }}
            placeholder="10-digit mobile number"
            required
          />

          <Button
            id="register-submit-btn"
            type="submit"
            variant="primary"
            loading={loading}
            className="btn--full py-3 mt-2"
          >
            Get OTP & Register →
          </Button>
        </form>

        <p className="text-center text-sm text-secondary mt-6">
          Already registered?{' '}
          <Link to="/otp-login" className="text-gold font-medium">
            Login here
          </Link>
        </p>
      </div>
    </div>
  )
}

export default RegisterPage
