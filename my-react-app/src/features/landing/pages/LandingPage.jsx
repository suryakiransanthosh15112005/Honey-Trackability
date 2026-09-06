import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Navbar from '../../../components/common/Navbar'
import Footer from '../../../components/common/Footer'
import Button from '../../../components/ui/Button'
import { useLanguage } from '../../../i18n/LanguageContext'
import VoiceButton from '../../../components/common/VoiceButton'

export const LandingPage = () => {
  const { t } = useLanguage()
  const [backendStatus, setBackendStatus] = useState(null)

  useEffect(() => {
    fetch('/api/health')
      .then((res) => res.json())
      .then((data) => setBackendStatus(data.success ? 'online' : 'error'))
      .catch(() => setBackendStatus('offline'))
  }, [])

  const voiceText = `${t('common.appName', 'HoneyChain')}. ${t('common.tagline', 'Blockchain-powered honey traceability')}. ${t('landing.heroDesc', 'From hive to table — every drop of honey verified, tested, and recorded on the blockchain.')}`

  return (
    <div className="landing">
      <div className="landing__bg-orb landing__bg-orb--tl" />
      <div className="landing__bg-orb landing__bg-orb--br" />

      <Navbar transparent={true} />

      {/* Hero */}
      <section id="hero" className="hero">
        {backendStatus && (
          <div className="hero__status-pill hero__status-pill--online">
            <span className={`hero__status-dot ${backendStatus === 'online' ? 'hero__status-dot--online' : 'hero__status-dot--offline'}`} />
            Backend {backendStatus === 'online' ? 'Online' : 'Offline'}
          </div>
        )}

        <div className="hero__icon-wrap flex items-center justify-center gap-3">
          <div className="hero__icon">🍯</div>
          <VoiceButton textToSpeak={voiceText} size="sm" />
        </div>

        <h1 className="hero__title">
          Honey<span className="text-gradient">Chain</span>
        </h1>

        <p className="hero__tagline">
          {t('common.tagline', 'Blockchain-powered honey traceability')}
        </p>

        <p className="hero__desc">
          {t('landing.heroDesc', 'From hive to table — every drop of honey verified, tested, and recorded on the blockchain.')}
        </p>

        <div className="hero__cta">
          <Link to="/otp-login" id="hero-beekeeper-btn">
            <Button variant="primary" size="lg">
              <span>🌿</span> {t('auth.otpLoginTitle', 'Beekeeper Login (OTP)')}
            </Button>
          </Link>
          <Link to="/login" id="hero-customer-btn">
            <Button variant="secondary" size="lg">
              <span>🛡️</span> {t('auth.loginTitle', 'Admin / Lab Login')}
            </Button>
          </Link>
        </div>
      </section>

      {/* Roles Overview */}
      <section id="features" className="features">
        <div className="features__inner">
          <h2 className="features__title">
            {t('landing.unifiedEcosystem', 'Unified Ecosystem')}
          </h2>
          <div className="features__grid">
            {[
              { icon: '🌿', title: t('auth.beekeeperRole', 'Beekeepers'), desc: t('landing.beekeeperDesc', 'Register hives, log harvests, and generate batches.') },
              { icon: '🔬', title: t('auth.labRole', 'Lab Technicians'), desc: t('landing.labDesc', 'Conduct tests, record purity, and issue certificates.') },
              { icon: '🛒', title: t('auth.customerRole', 'Customers'), desc: t('landing.customerDesc', 'Scan QR codes and verify pure origin of every jar.') },
              { icon: '⚖️', title: t('auth.kvicRole', 'KVIC / Admin'), desc: t('landing.adminDesc', 'Audit compliance and supervise the platform.') },
            ].map(({ icon, title, desc }) => (
              <div key={title} className="feature-card">
                <div className="feature-card__icon">{icon}</div>
                <h3 className="feature-card__title">{title}</h3>
                <p className="feature-card__desc">{desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <Footer />
    </div>
  )
}

export default LandingPage
