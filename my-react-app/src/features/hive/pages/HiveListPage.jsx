import React, { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import HiveCard from '../components/HiveCard'
import HiveForm from '../components/HiveForm'
import PageHeader from '../../../components/layout/PageHeader'
import Alert from '../../../components/feedback/Alert'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import VoiceButton from '../../../components/common/VoiceButton'
import { useHives } from '../hooks/useHives'
import { useLanguage } from '../../../i18n/LanguageContext'

export const HiveListPage = () => {
  const { hives, loading, error, fetchHives, createHive, updateHiveStatus, clearError } = useHives()
  const { t } = useLanguage()
  const [showAddForm, setShowAddForm] = useState(false)
  const [successMsg, setSuccessMsg] = useState(null)
  const navigate = useNavigate()

  useEffect(() => {
    fetchHives()
  }, [])

  const handleCreate = async (formData) => {
    const result = await createHive(formData)
    if (!result.error) {
      const code = result.payload?.hiveCode || 'new hive'
      setShowAddForm(false)
      setSuccessMsg(t('success.hiveCreated', 'Hive registered successfully!'))
      setTimeout(() => setSuccessMsg(null), 4000)
    }
  }

  const handleDeactivate = async (id) => {
    await updateHiveStatus(id, 'INACTIVE')
  }

  const handleActivate = async (id) => {
    await updateHiveStatus(id, 'ACTIVE')
  }

  const activeCount = hives.filter(h => h.status === 'ACTIVE').length
  const alertCount = hives.filter(h => h.status === 'ALERT').length

  return (
    <BeekeeperLayout>
      <div className="space-y-6">
        {/* Page Header */}
        <PageHeader
          title={t('navigation.myHives', 'My Hives')}
          subtitle={
            <span>
              {hives.length} {t('units.hives', 'hives')} · {activeCount} {t('hive.active', 'active')}
              {alertCount > 0 && <span className="text-amber-800 font-bold ml-2">· {alertCount} {t('hive.alert', 'alert')}</span>}
            </span>
          }
          actions={
            <>
              <VoiceButton translationKey="hive.myHivesSub" />
              <Button
                id="add-hive-btn"
                variant="primary"
                onClick={() => setShowAddForm(!showAddForm)}
                className="flex items-center gap-2 font-bold"
              >
                <span>{showAddForm ? '✕' : '+'}</span>
                {showAddForm ? t('common.cancel', 'Cancel') : t('hive.addHive', '+ Add New Hive')}
              </Button>
            </>
          }
        />

        {/* Feedback */}
        {error && <Alert type="error" message={error} onClose={clearError} />}
        {successMsg && <Alert type="success" message={successMsg} onClose={() => setSuccessMsg(null)} />}

        {/* Add Hive Form (inline) */}
        {showAddForm && (
          <Card className="p-6">
            <h2 className="text-lg font-bold text-slate-900 font-['Outfit'] mb-5 flex items-center gap-2">
              <span>🐝</span> {t('hive.addHive', 'Register New Hive')}
            </h2>
            <HiveForm
              onSubmit={handleCreate}
              loading={loading}
              onCancel={() => setShowAddForm(false)}
              submitLabel={t('hive.addHive', 'Add Hive')}
            />
          </Card>
        )}

        {/* Hive Grid */}
        {loading && hives.length === 0 ? (
          <LoadingSpinner text={t('loading.loading', 'Loading your hives...')} />
        ) : hives.length === 0 ? (
          <Card className="text-center py-16 space-y-4">
            <div className="text-6xl">🐝</div>
            <h2 className="text-2xl font-bold text-slate-900 font-['Outfit']">{t('hive.noHives', 'No Hives Yet')}</h2>
            <p className="text-slate-500 text-sm max-w-sm mx-auto">
              {t('hive.myHivesSub', 'Register your first hive to start tracking harvests and honey production.')}
            </p>
            <div className="pt-2">
              <Button variant="primary" onClick={() => setShowAddForm(true)}>
                {t('hive.addHive', '+ Add Your First Hive')}
              </Button>
            </div>
          </Card>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {hives.map((hive) => (
              <HiveCard
                key={hive.id}
                hive={hive}
                onDeactivate={handleDeactivate}
                onActivate={handleActivate}
              />
            ))}
          </div>
        )}
      </div>
    </BeekeeperLayout>
  )
}

export default HiveListPage
