import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import Alert from '../../../components/feedback/Alert'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import ProfileForm from '../components/ProfileForm'
import VoiceButton from '../../../components/common/VoiceButton'
import { useBeekeeperProfile } from '../hooks/useBeekeeperProfile'
import { useLanguage } from '../../../i18n/LanguageContext'

export const BeekeeperProfilePage = () => {
  const { profile, loading, fetched, error, updateProfile, clearError } = useBeekeeperProfile(true)
  const { t, formatDate } = useLanguage()
  const [isEditing, setIsEditing] = useState(false)
  const [updateSuccess, setUpdateSuccess] = useState(false)

  const handleUpdate = async (formData) => {
    const result = await updateProfile(formData)
    if (!result.error) {
      setIsEditing(false)
      setUpdateSuccess(true)
      setTimeout(() => setUpdateSuccess(false), 4000)
    }
  }

  const getStatusBadge = (status) => {
    switch (status) {
      case 'APPROVED':
        return (
          <span className="px-3 py-1 rounded-full text-xs font-bold border border-blue-200 bg-blue-50 text-blue-700 flex items-center gap-1.5 font-mono shadow-xs">
            <span className="w-2 h-2 rounded-full bg-blue-600 animate-pulse" />
            {t('profile.statusApproved', 'APPROVED')}
          </span>
        )
      case 'REJECTED':
        return (
          <span className="px-3 py-1 rounded-full text-xs font-bold border border-slate-300 bg-slate-100 text-slate-800 flex items-center gap-1.5 font-mono">
            <span className="w-2 h-2 rounded-full bg-slate-600" />
            {t('profile.statusRejected', 'REJECTED')}
          </span>
        )
      default:
        return (
          <span className="px-3 py-1 rounded-full text-xs font-bold border border-amber-300 bg-amber-50 text-amber-900 flex items-center gap-1.5 font-mono shadow-xs">
            <span className="w-2 h-2 rounded-full bg-amber-500 animate-pulse" />
            {t('profile.statusPending', 'PENDING')}
          </span>
        )
    }
  }

  return (
    <BeekeeperLayout>
      <div className="w-full space-y-6">
        {/* Header with action */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-2 border-b border-slate-200">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-bold bg-amber-50 text-amber-900 border border-amber-200">
                📜 KVIC Certified Beekeeper
              </span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-black text-slate-900 font-['Outfit']">
              {t('navigation.beekeepers', 'Beekeeper')}{' '}
              <span className="text-blue-600 font-extrabold">{t('profile.title', 'Profile')}</span>
            </h1>
            <p className="text-slate-600 text-xs sm:text-sm mt-0.5">
              {t('profile.sub', 'Manage your registered apiary details and language preferences')}
            </p>
          </div>

          <div className="flex items-center gap-2.5 flex-wrap">
            <VoiceButton translationKey="profile.sub" />
            {!isEditing && profile && (
              <Button
                id="edit-profile-btn"
                variant="primary"
                size="sm"
                onClick={() => setIsEditing(true)}
                className="flex items-center gap-2"
              >
                <span>✏️</span> {t('common.edit', 'Edit Profile')}
              </Button>
            )}
          </div>
        </div>

        {error && <Alert type="danger" message={error} onClose={clearError} />}
        {updateSuccess && (
          <Alert type="success" message={t('profile.updateSuccess', 'Profile updated successfully!')} />
        )}

        {/* Loading State */}
        {loading && !fetched ? (
          <div className="py-16 text-center">
            <LoadingSpinner text={t('loading.loading', 'Loading your beekeeper profile...')} />
          </div>
        ) : !profile ? (
          /* Profile Not Found -> Prompt Onboarding */
          <Card className="text-center py-14 px-6 space-y-4 max-w-xl mx-auto">
            <div className="w-16 h-16 rounded-full bg-amber-50 border border-amber-200 flex items-center justify-center mx-auto text-3xl">
              🌿
            </div>
            <div className="space-y-1">
              <h2 className="text-2xl font-bold text-slate-900 font-['Outfit']">
                {t('empty.noData', 'No Profile Found')}
              </h2>
              <p className="text-slate-600 max-w-md mx-auto text-xs sm:text-sm leading-relaxed">
                {t(
                  'onboarding.warningSub',
                  'Please complete your KVIC beekeeper profile to enable harvest logging, IoT telemetry, and verified honey sales.'
                )}
              </p>
            </div>
            <div className="pt-2">
              <Link to="/beekeeper/onboarding">
                <Button variant="primary" size="lg" className="font-bold">
                  {t('onboarding.completeSetup', 'Start Onboarding →')}
                </Button>
              </Link>
            </div>
          </Card>
        ) : isEditing ? (
          /* Edit Mode */
          <Card className="p-6 sm:p-8 max-w-3xl mx-auto">
            <div className="flex items-center justify-between mb-6 pb-4 border-b border-slate-100">
              <h2 className="text-xl font-bold text-slate-900 font-['Outfit']">
                {t('common.edit', 'Edit Profile Details')}
              </h2>
              <span className="text-xs text-slate-600 font-medium">KVIC ID is permanent</span>
            </div>
            <ProfileForm
              initialValues={profile}
              onSubmit={handleUpdate}
              loading={loading}
              onCancel={() => setIsEditing(false)}
            />
          </Card>
        ) : (
          /* View Mode */
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
            {/* Identity Card */}
            <Card className="lg:col-span-4 flex flex-col items-center text-center p-6 space-y-4 bg-white border border-slate-200/90 shadow-sm">
              <div className="relative">
                {profile.photoUrl ? (
                  <img
                    src={profile.photoUrl}
                    alt={profile.name}
                    className="w-28 h-28 rounded-full object-cover border-4 border-amber-200 shadow-md"
                  />
                ) : (
                  <div className="w-28 h-28 rounded-full bg-amber-100 border-4 border-amber-200 flex items-center justify-center text-5xl shadow-md">
                    👨‍🌾
                  </div>
                )}
              </div>

              <div className="space-y-0.5">
                <h2 className="text-xl font-bold text-slate-900 font-['Outfit']">{profile.name}</h2>
                <p className="text-slate-600 text-sm font-medium">{profile.village || 'Registered Village'}</p>
                <p className="text-slate-600 text-xs font-mono">{profile.phoneNumber}</p>
              </div>

              <div className="pt-1">{getStatusBadge(profile.verificationStatus)}</div>

              <div className="w-full pt-4 border-t border-slate-100 text-xs text-slate-600 space-y-1 text-left">
                <span className="block font-bold text-slate-800 uppercase text-[10px] tracking-wider">
                  Quick Navigation
                </span>
                <div className="flex flex-col gap-1.5 pt-1">
                  <Link
                    to="/beekeeper/hives"
                    className="text-blue-600 hover:underline flex items-center justify-between"
                  >
                    <span>🐝 My Hives & Colonies</span>
                    <span>→</span>
                  </Link>
                  <Link
                    to="/beekeeper/batches"
                    className="text-blue-600 hover:underline flex items-center justify-between"
                  >
                    <span>🍯 Logged Harvest Batches</span>
                    <span>→</span>
                  </Link>
                  <Link
                    to="/beekeeper/earnings"
                    className="text-blue-600 hover:underline flex items-center justify-between"
                  >
                    <span>💰 Earnings & Payouts</span>
                    <span>→</span>
                  </Link>
                </div>
              </div>
            </Card>

            {/* Details Card */}
            <Card className="lg:col-span-8 p-6 sm:p-8 space-y-6 bg-white border border-slate-200/90 shadow-sm">
              <h3 className="text-lg font-bold text-slate-900 font-['Outfit'] flex items-center gap-2 border-b border-slate-100 pb-3">
                <span>📋</span> {t('profile.verificationStatus', 'Verification Status')} & Apiary Details
              </h3>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="p-4 rounded-xl bg-amber-50 border border-amber-200 shadow-xs">
                  <p className="text-xs text-slate-600 font-medium">{t('onboarding.kvicId', 'KVIC Registration ID')}</p>
                  <p className="text-lg font-bold text-amber-900 font-mono mt-1">{profile.kvicId}</p>
                </div>

                <div className="p-4 rounded-xl bg-slate-50 border border-slate-200 shadow-xs">
                  <p className="text-xs text-slate-600 font-medium">{t('onboarding.language', 'Preferred Language')}</p>
                  <p className="text-lg font-bold text-slate-900 mt-1">{profile.preferredLanguage || 'English'}</p>
                </div>

                <div className="p-4 rounded-xl bg-slate-50 border border-slate-200 shadow-xs">
                  <p className="text-xs text-slate-600 font-medium">Apiary Location Coordinates</p>
                  <p className="text-sm font-semibold text-slate-900 font-mono mt-1">
                    {profile.latitude && profile.longitude
                      ? `${profile.latitude}° N, ${profile.longitude}° E`
                      : 'Coordinates not specified'}
                  </p>
                </div>

                <div className="p-4 rounded-xl bg-slate-50 border border-slate-200 shadow-xs">
                  <p className="text-xs text-slate-600 font-medium">{t('common.date', 'Member Since')}</p>
                  <p className="text-sm font-semibold text-slate-900 mt-1">
                    {profile.createdAt ? formatDate(profile.createdAt) : 'Recently registered'}
                  </p>
                </div>
              </div>
            </Card>
          </div>
        )}
      </div>
    </BeekeeperLayout>
  )
}

export default BeekeeperProfilePage
