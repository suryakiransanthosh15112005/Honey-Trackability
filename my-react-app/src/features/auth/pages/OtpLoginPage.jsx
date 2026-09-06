import React, { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { useLanguage } from '../../../i18n/LanguageContext'
import Input from '../../../components/ui/Input'
import Button from '../../../components/ui/Button'
import Alert from '../../../components/feedback/Alert'
import LanguageSelector from '../../../components/common/LanguageSelector'
import { ROLES, ROLE_ROUTES } from '../../../constants/roles'

export const OtpLoginPage = () => {
  const [phoneNumber, setPhoneNumber] = useState('')
  const [otp, setOtp] = useState('')
  const [selectedRole, setSelectedRole] = useState(ROLES.BEEKEEPER)

  const {
    sendOtp,
    verifyOtp,
    loading,
    error,
    otpSent,
    isAuthenticated,
    role,
    clearError,
    resetOtpState,
  } = useAuth()

  const { t } = useLanguage()
  const navigate = useNavigate()

  useEffect(() => {
    if (isAuthenticated && role) {
      const target = ROLE_ROUTES[role] || '/'
      navigate(target, { replace: true })
    }
  }, [isAuthenticated, role, navigate])

  const handleSendOtp = async (e) => {
    e.preventDefault()
    if (!phoneNumber) return
    await sendOtp({ phoneNumber, role: selectedRole })
  }

  const handleVerifyOtp = async (e) => {
    e.preventDefault()
    if (!otp) return
    await verifyOtp({ phoneNumber, otp, role: selectedRole })
  }

  const handleQuickBeekeeper = () => {
    setPhoneNumber('9876543213')
    setSelectedRole(ROLES.BEEKEEPER)
    if (error) clearError()
  }

  const handleQuickCustomer = () => {
    setPhoneNumber('9876543214')
    setSelectedRole(ROLES.CUSTOMER)
    if (error) clearError()
  }

  return (
    <div className="auth-page">
      <div className="auth-page__orb auth-page__orb--tl" />
      <div className="auth-page__orb auth-page__orb--br" />

      {/* Language Switcher */}
      <div className="absolute top-4 right-6 z-50">
        <LanguageSelector />
      </div>

      <div className="auth-card">
        {/* Header */}
        <div className="auth-header">
          <Link to="/" className="inline-flex flex-col items-center gap-2">
            <div className="auth-logo-icon">🍯</div>
            <span className="text-gradient font-black text-xl font-brand">{t('common.appName', 'HoneyChain')}</span>
          </Link>
          <h1 className="auth-header__title">{t('auth.otpLoginTitle', 'OTP Login')}</h1>
          <p className="auth-header__sub">{t('auth.otpLoginSub', 'Instant passwordless access via SMS')}</p>
        </div>

        {error && <Alert type="error" message={error} onClose={clearError} className="mb-4" />}

        {!otpSent ? (
          /* Step 1: Phone + Role */
          <form id="send-otp-form" onSubmit={handleSendOtp} className="flex-col gap-4">
            <div>
              <label className="form-label mb-2">{t('auth.selectRole', 'Select Your Role')}</label>
              <div className="role-grid">
                <button
                  type="button"
                  onClick={() => setSelectedRole(ROLES.BEEKEEPER)}
                  className={`role-btn${selectedRole === ROLES.BEEKEEPER ? ' role-btn--active' : ''}`}
                >
                  🌿 {t('auth.beekeeperRole', 'Beekeeper')}
                </button>
                <button
                  type="button"
                  onClick={() => setSelectedRole(ROLES.CUSTOMER)}
                  className={`role-btn${selectedRole === ROLES.CUSTOMER ? ' role-btn--active' : ''}`}
                >
                  🛒 {t('auth.customerRole', 'Customer')}
                </button>
              </div>
            </div>

            <Input
              id="otp-phone-input"
              label={t('auth.phoneLabel', 'Mobile Phone Number')}
              value={phoneNumber}
              onChange={(e) => {
                setPhoneNumber(e.target.value)
                if (error) clearError()
              }}
              placeholder={t('auth.phonePlaceholder', '10-digit mobile number')}
              required
            />

            <Button
              id="send-otp-btn"
              type="submit"
              variant="primary"
              loading={loading}
              className="btn--full py-3 mt-2"
            >
              {t('auth.sendOtp', 'Send OTP →')}
            </Button>
          </form>
        ) : (
          /* Step 2: Verify OTP */
          <form id="verify-otp-form" onSubmit={handleVerifyOtp} className="flex-col gap-4">
            <div className="otp-sent-banner">
              {t('auth.otpSentBanner', { phone: phoneNumber, code: '123456' })}
            </div>

            <Input
              id="otp-code-input"
              label={t('auth.enterOtp', 'Enter 6-Digit OTP')}
              value={otp}
              onChange={(e) => {
                setOtp(e.target.value)
                if (error) clearError()
              }}
              placeholder={t('auth.otpPlaceholder', '123456')}
              maxLength={6}
              required
            />

            <Button
              id="verify-otp-btn"
              type="submit"
              variant="primary"
              loading={loading}
              className="btn--full py-3"
            >
              {t('auth.verifyOtp', 'Verify & Login')}
            </Button>

            <button
              type="button"
              onClick={resetOtpState}
              className="auth-link-btn"
            >
              {t('auth.changePhone', '← Change Phone Number')}
            </button>
          </form>
        )}

        {/* Switch to Password */}
        <div className="auth-divider">
          <div className="auth-divider__line" />
          <span className="auth-divider__text">{t('common.or', 'OR')}</span>
          <div className="auth-divider__line" />
        </div>

        <Link to="/login" id="switch-to-password-btn">
          <button className="auth-switch-btn">
            {t('auth.switchToPassword', '🛡️ Admin / Lab? Login with Password')}
          </button>
        </Link>

        {/* Quick Fills */}
        <div className="quick-fills">
          <p className="quick-fills__label">{t('auth.quickFill', 'Role Sign-in Presets:')}</p>
          <div className="quick-fills__grid">
            <button
              type="button"
              onClick={handleQuickBeekeeper}
              className="quick-fill-chip"
            >
              🌿 {t('auth.beekeeperRole', 'Beekeeper')} (9876543213)
            </button>
            <button
              type="button"
              onClick={handleQuickCustomer}
              className="quick-fill-chip"
            >
              🛒 {t('auth.customerRole', 'Customer')} (9876543214)
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default OtpLoginPage
