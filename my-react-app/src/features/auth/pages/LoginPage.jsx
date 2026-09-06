import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { useLanguage } from '../../../i18n/LanguageContext'
import Input from '../../../components/ui/Input'
import Button from '../../../components/ui/Button'
import Alert from '../../../components/feedback/Alert'
import LanguageSelector from '../../../components/common/LanguageSelector'
import { ROLES, ROLE_ROUTES } from '../../../constants/roles'

export const LoginPage = ({ initialRole }) => {
  const [formData, setFormData] = useState({ phoneNumber: '', password: '' })
  const [activeRoleTab, setActiveRoleTab] = useState(
    initialRole === ROLES.LAB ? ROLES.LAB : ROLES.ADMIN
  )
  const [prevInitialRole, setPrevInitialRole] = useState(initialRole)

  if (initialRole !== prevInitialRole) {
    setPrevInitialRole(initialRole)
    if (initialRole === ROLES.ADMIN || initialRole === ROLES.LAB) {
      setActiveRoleTab(initialRole)
    }
  }

  const { loginWithPassword, loading, error, isAuthenticated, role, clearError } = useAuth()
  const { t } = useLanguage()
  const navigate = useNavigate()

  useEffect(() => {
    if (isAuthenticated && role) {
      const targetRoute = ROLE_ROUTES[role] || '/'
      navigate(targetRoute, { replace: true })
    }
  }, [isAuthenticated, role, navigate])

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
    if (error) clearError()
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    await loginWithPassword(formData)
  }

  const handleQuickFill = (phone, pass, selectedRole) => {
    setFormData({ phoneNumber: phone, password: pass })
    if (selectedRole) setActiveRoleTab(selectedRole)
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
          <h1 className="auth-header__title">{t('auth.loginTitle', 'Sign In to HoneyChain')}</h1>
          <p className="auth-header__sub">{t('auth.loginSub', 'Secure Role-Based Portal Access')}</p>
        </div>

        {error && <Alert type="error" message={error} onClose={clearError} className="mb-4" />}

        {/* Form */}
        <form id="login-form" onSubmit={handleSubmit} className="flex-col gap-4">
          <div>
            <label className="form-label mb-2">{t('auth.selectRole', 'Select Your Role')}</label>
            <div className="role-grid">
              <button
                type="button"
                onClick={() => {
                  setActiveRoleTab(ROLES.ADMIN)
                  if (error) clearError()
                }}
                className={`role-btn${activeRoleTab === ROLES.ADMIN ? ' role-btn--active' : ''}`}
              >
                🛡️ {t('auth.adminRole', 'Admin')}
              </button>
              <button
                type="button"
                onClick={() => {
                  setActiveRoleTab(ROLES.LAB)
                  if (error) clearError()
                }}
                className={`role-btn${activeRoleTab === ROLES.LAB ? ' role-btn--active' : ''}`}
              >
                🔬 {t('auth.labRoleShort', 'Lab')}
              </button>
            </div>
          </div>

          <Input
            id="login-phone"
            name="phoneNumber"
            label={t('auth.phoneLabel', 'Mobile Phone Number')}
            value={formData.phoneNumber}
            onChange={handleChange}
            placeholder={t('auth.phonePlaceholder', 'e.g. 9876543210')}
            required
          />

          <Input
            id="login-password"
            name="password"
            type="password"
            label={t('auth.passwordLabel', 'Password')}
            value={formData.password}
            onChange={handleChange}
            placeholder={t('auth.passwordPlaceholder', '••••••••')}
            required
          />

          <Button
            id="login-submit-btn"
            type="submit"
            variant="primary"
            loading={loading}
            className="btn--full py-3 mt-2"
          >
            {t('navigation.login', 'Sign In')}
          </Button>
        </form>

        {/* Switch to OTP */}
        <div className="auth-divider">
          <div className="auth-divider__line" />
          <span className="auth-divider__text">{t('common.or', 'OR')}</span>
          <div className="auth-divider__line" />
        </div>

        <Link to="/otp-login" id="switch-to-otp-btn">
          <button className="auth-switch-btn">
            {t('auth.switchToOtp', '📲 Beekeeper / Customer? Login with OTP')}
          </button>
        </Link>

        {/* Quick test credentials */}
        <div className="quick-fills">
          <p className="quick-fills__label">{t('auth.quickFill', 'Role Sign-in Presets:')}</p>
          <div className="quick-fills__grid">
            <button
              type="button"
              onClick={() => handleQuickFill('9876543210', 'Admin@123', 'ADMIN')}
              className="quick-fill-chip"
            >
              🛡️ {t('auth.adminRole', 'Admin')}
            </button>
            <button
              type="button"
              onClick={() => handleQuickFill('9876543212', 'Lab@123', 'LAB')}
              className="quick-fill-chip"
            >
              🔬 {t('auth.labRole', 'Lab Tech')}
            </button>
            <button
              type="button"
              onClick={() => handleQuickFill('9876543211', 'Kvic@123', 'KVIC_OFFICER')}
              className="quick-fill-chip"
            >
              ⚖️ {t('auth.kvicRole', 'KVIC')}
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default LoginPage
