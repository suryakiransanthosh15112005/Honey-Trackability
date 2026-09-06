import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import { useBeekeeperProfile } from '../hooks/useBeekeeperProfile'
import { useLanguage } from '../../../i18n/LanguageContext'
import LocationSelector from '../components/LocationSelector'
import GlobalLanguageSelector from '../../../components/common/LanguageSelector'
import VoiceButton from '../../../components/common/VoiceButton'
import Input from '../../../components/ui/Input'
import Button from '../../../components/ui/Button'
import Card from '../../../components/ui/Card'
import Alert from '../../../components/feedback/Alert'

const TOTAL_STEPS = 6

export const BeekeeperOnboardingPage = () => {
  const [currentStep, setCurrentStep] = useState(1)
  const [formData, setFormData] = useState({
    kvicId: '',
    name: '',
    village: '',
    photoUrl: '',
    latitude: null,
    longitude: null,
    preferredLanguage: 'TAMIL',
  })
  const [stepErrors, setStepErrors] = useState({})
  const [successComplete, setSuccessComplete] = useState(false)

  const { createProfile, loading, error, clearError } = useBeekeeperProfile()
  const { t } = useLanguage()
  const navigate = useNavigate()

  const validateCurrentStep = () => {
    const errs = {}
    if (currentStep === 1) {
      if (!formData.kvicId.trim()) errs.kvicId = t('validation.required', 'KVIC ID is required')
    } else if (currentStep === 2) {
      if (!formData.name.trim()) errs.name = t('validation.required', 'Full name is required')
      if (!formData.village.trim()) errs.village = t('validation.required', 'Village/Town name is required')
    } else if (currentStep === 3) {
      if (!formData.village.trim()) errs.village = t('validation.required', 'Village name is required')
    }
    setStepErrors(errs)
    return Object.keys(errs).length === 0
  }

  const handleNext = () => {
    if (clearError) clearError()
    if (!validateCurrentStep()) return
    if (currentStep < TOTAL_STEPS) {
      setCurrentStep((prev) => prev + 1)
    }
  }

  const handlePrev = () => {
    if (currentStep > 1) {
      setCurrentStep((prev) => prev - 1)
    }
  }

  const handleSubmit = async () => {
    if (!validateCurrentStep()) return
    const result = await createProfile(formData)
    if (!result.error) {
      setSuccessComplete(true)
      setTimeout(() => {
        navigate('/beekeeper/dashboard')
      }, 1500)
    }
  }

  return (
    <BeekeeperLayout>
      <div className="w-full py-4 relative">
        {/* Background glow */}
        <div className="fixed -top-24 -left-24 w-96 h-96 sm:w-128 sm:h-128 rounded-full bg-blue-500/5 blur-3xl pointer-events-none" />

        {/* Top Header */}
        <header className="max-w-3xl mx-auto w-full text-center mb-6">
          <div className="flex items-center justify-between mb-4">
            <div className="inline-flex items-center gap-2">
              <span className="text-2xl">🍯</span>
              <span className="font-extrabold text-xl font-['Outfit'] text-blue-600">{t('common.appName', 'HoneyChain')}</span>
            </div>
            <GlobalLanguageSelector />
          </div>
          <h1 className="text-2xl sm:text-3xl font-black text-slate-900 font-['Outfit']">{t('onboarding.title', 'Beekeeper Setup')}</h1>
          <p className="text-slate-500 text-sm mt-1">{t('onboarding.sub', 'Complete your identity and apiary verification')}</p>

          {/* Progress Bar */}
          <div className="mt-6 max-w-lg mx-auto">
            <div className="flex items-center justify-between text-xs font-semibold text-blue-700 mb-2">
              <span>Step {currentStep} of {TOTAL_STEPS}</span>
              <span>{Math.round((currentStep / TOTAL_STEPS) * 100)}% Completed</span>
            </div>
            <div className="w-full h-2.5 rounded-full bg-slate-200 overflow-hidden">
              <div
                className="h-full bg-blue-600 transition-all duration-300 rounded-full"
                style={{ width: `${(currentStep / TOTAL_STEPS) * 100}%` }}
              />
            </div>
          </div>
        </header>

        {/* Main Step Grid on Desktop */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start max-w-5xl mx-auto">
          {/* Main Form Step Area */}
          <main className="lg:col-span-8 w-full">
            <Card className="p-6 sm:p-8 shadow-xl bg-white border border-slate-200/90 rounded-3xl">
              <div className="flex justify-end mb-4">
                <VoiceButton translationKey="onboarding.sub" />
              </div>

              {error && <Alert type="error" message={error} onClose={clearError} className="mb-6" />}

            {successComplete ? (
              <div className="text-center py-10 space-y-4 animate-fade-in">
                <div className="text-6xl animate-bounce">🎉</div>
                <h2 className="text-2xl font-bold text-slate-900 font-['Outfit']">{t('success.profileSaved', 'Profile Created Successfully!')}</h2>
                <p className="text-slate-500 text-sm">{t('loading.loading', 'Redirecting to your Beekeeper Dashboard...')}</p>
              </div>
            ) : (
              <div>
                {/* Step 1: KVIC ID */}
                {currentStep === 1 && (
                  <div className="space-y-6 animate-fade-in">
                    <div className="flex items-center gap-3">
                      <span className="text-4xl">📜</span>
                      <div>
                        <h2 className="text-xl font-bold text-slate-900 font-['Outfit']">{t('onboarding.kvicId', 'Enter your KVIC ID')}</h2>
                        <p className="text-slate-500 text-xs">Assigned by Khadi & Village Industries Commission</p>
                      </div>
                    </div>

                    <Input
                      id="onboarding-kvic-id"
                      label={t('onboarding.kvicId', 'KVIC ID Number *')}
                      value={formData.kvicId}
                      onChange={(e) => setFormData({ ...formData, kvicId: e.target.value.toUpperCase() })}
                      placeholder={t('onboarding.kvicPlaceholder', 'e.g. KVIC-BH-88421')}
                      error={stepErrors.kvicId}
                      className="text-lg uppercase tracking-wider"
                      required
                    />
                  </div>
                )}

                {/* Step 2: Name & Village */}
                {currentStep === 2 && (
                  <div className="space-y-6 animate-fade-in">
                    <div className="flex items-center gap-3">
                      <span className="text-4xl">👤</span>
                      <div>
                        <h2 className="text-xl font-bold text-slate-900 font-['Outfit']">{t('onboarding.fullName', 'Personal Information')}</h2>
                        <p className="text-slate-500 text-xs">Appears on public honey batch certificates</p>
                      </div>
                    </div>

                    <Input
                      id="onboarding-name"
                      label={t('onboarding.fullName', 'Full Name *')}
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                      placeholder={t('onboarding.namePlaceholder', 'e.g. Ramesh Kumar')}
                      error={stepErrors.name}
                      className="text-lg"
                      required
                    />

                    <Input
                      id="onboarding-village"
                      label={t('onboarding.village', 'Village / Base Town *')}
                      value={formData.village}
                      onChange={(e) => setFormData({ ...formData, village: e.target.value })}
                      placeholder={t('onboarding.villagePlaceholder', 'e.g. Rampur, Bihar')}
                      error={stepErrors.village}
                      className="text-lg"
                      required
                    />
                  </div>
                )}

                {/* Step 3: Location */}
                {currentStep === 3 && (
                  <div className="space-y-6 animate-fade-in">
                    <div className="flex items-center gap-3">
                      <span className="text-4xl">📍</span>
                      <div>
                        <h2 className="text-xl font-bold text-slate-900 font-['Outfit']">{t('verification.beekeeperInfo', 'Apiary Location')}</h2>
                        <p className="text-slate-500 text-xs">Provides honey origin proof for consumers</p>
                      </div>
                    </div>

                    <LocationSelector
                      village={formData.village}
                      latitude={formData.latitude}
                      longitude={formData.longitude}
                      onVillageChange={(v) => setFormData({ ...formData, village: v })}
                      onLocationChange={(lat, lng) => setFormData({ ...formData, latitude: lat, longitude: lng })}
                      errors={stepErrors}
                    />
                  </div>
                )}

                {/* Step 4: Language */}
                {currentStep === 4 && (
                  <div className="space-y-6 animate-fade-in">
                    <div className="flex items-center gap-3">
                      <span className="text-4xl">🗣️</span>
                      <div>
                        <h2 className="text-xl font-bold text-slate-900 font-['Outfit']">{t('onboarding.language', 'Preferred Language')}</h2>
                        <p className="text-slate-500 text-xs">Select your preferred app and SMS notification language</p>
                      </div>
                    </div>
                  </div>
                )}

                {/* Step 5: Optional Photo */}
                {currentStep === 5 && (
                  <div className="space-y-6 animate-fade-in">
                    <div className="flex items-center gap-3">
                      <span className="text-4xl">📸</span>
                      <div>
                        <h2 className="text-xl font-bold text-slate-900 font-['Outfit']">{t('profile.uploadPhoto', 'Profile Photo (Optional)')}</h2>
                        <p className="text-slate-500 text-xs">Builds trust with marketplace buyers</p>
                      </div>
                    </div>

                    <Input
                      id="onboarding-photo"
                      label="Photo URL"
                      value={formData.photoUrl}
                      onChange={(e) => setFormData({ ...formData, photoUrl: e.target.value })}
                      placeholder="https://..."
                    />
                  </div>
                )}

                {/* Step 6: Review & Submit */}
                {currentStep === 6 && (
                  <div className="space-y-6 animate-fade-in">
                    <div className="flex items-center gap-3">
                      <span className="text-4xl">🔍</span>
                      <div>
                        <h2 className="text-xl font-bold text-slate-900 font-['Outfit']">Review Details</h2>
                        <p className="text-slate-500 text-xs">Confirm your information before submission</p>
                      </div>
                    </div>

                    <div className="space-y-3 p-4 rounded-2xl bg-amber-50 border border-amber-200 text-sm shadow-sm">
                      <div className="flex justify-between py-1 border-b border-amber-200/60">
                        <span className="text-slate-600">{t('onboarding.kvicId', 'KVIC ID')}:</span>
                        <span className="text-slate-900 font-mono font-bold">{formData.kvicId}</span>
                      </div>
                      <div className="flex justify-between py-1 border-b border-amber-200/60">
                        <span className="text-slate-600">{t('onboarding.fullName', 'Name')}:</span>
                        <span className="text-slate-900 font-semibold">{formData.name}</span>
                      </div>
                      <div className="flex justify-between py-1 border-b border-amber-200/60">
                        <span className="text-slate-600">{t('onboarding.village', 'Village')}:</span>
                        <span className="text-slate-900">{formData.village}</span>
                      </div>
                    </div>
                  </div>
                )}

                {/* Navigation Controls */}
                <div className="flex items-center gap-3 mt-8 pt-4 border-t border-slate-100">
                  {currentStep > 1 && (
                    <Button
                      type="button"
                      variant="secondary"
                      onClick={handlePrev}
                      className="py-3 px-6 font-semibold"
                    >
                      {t('common.back', '← Back')}
                    </Button>
                  )}

                  {currentStep < TOTAL_STEPS ? (
                    <Button
                      id="onboarding-continue-btn"
                      type="button"
                      variant="primary"
                      onClick={handleNext}
                      className="flex-1 py-3 bg-blue-600 hover:bg-blue-700 border-blue-600 font-bold text-white"
                    >
                      {t('common.continue', 'Continue →')}
                    </Button>
                  ) : (
                    <Button
                      id="onboarding-submit-btn"
                      type="button"
                      variant="primary"
                      onClick={handleSubmit}
                      loading={loading}
                      className="flex-1 py-3 bg-blue-600 hover:bg-blue-700 border-blue-600 font-bold text-white"
                    >
                      {t('onboarding.submitOnboarding', 'Create Profile ✨')}
                    </Button>
                  )}
                </div>
              </div>
            )}
          </Card>
        </main>

        {/* Sidebar Guidance Column on Desktop */}
        <aside className="lg:col-span-4 space-y-4">
          <Card className="p-5 border border-amber-200/80 bg-gradient-to-br from-amber-50/60 to-white space-y-3">
            <div className="flex items-center gap-2">
              <span className="text-xl">🌟</span>
              <h3 className="font-bold text-slate-900 font-['Outfit'] text-sm">Why Onboarding Matters</h3>
            </div>
            <ul className="text-xs text-slate-600 space-y-2 leading-relaxed">
              <li className="flex items-start gap-1.5">
                <span className="text-amber-600 font-bold">✓</span>
                <span><strong>Direct Payouts:</strong> Sell pure honey directly to consumers at guaranteed fair floor prices.</span>
              </li>
              <li className="flex items-start gap-1.5">
                <span className="text-amber-600 font-bold">✓</span>
                <span><strong>Cryptographic Trust:</strong> Every jar is tied to your verified apiary coordinates.</span>
              </li>
              <li className="flex items-start gap-1.5">
                <span className="text-amber-600 font-bold">✓</span>
                <span><strong>Free IoT Telemetry:</strong> Connect smart hive sensors to monitor weight, moisture, and colony health.</span>
              </li>
            </ul>
          </Card>
        </aside>
      </div>

      {/* Footer info */}
      <footer className="max-w-xl mx-auto w-full text-center text-xs text-slate-500 mt-8 font-medium">
        HoneyChain • National Honey Traceability Platform
      </footer>
    </div>
  </BeekeeperLayout>
  )
}

export default BeekeeperOnboardingPage
