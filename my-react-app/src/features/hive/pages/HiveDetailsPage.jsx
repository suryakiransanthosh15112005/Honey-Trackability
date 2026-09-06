import React, { useEffect, useState } from 'react'
import { useParams, Link, useLocation } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import HiveStatusBadge from '../components/HiveStatusBadge'
import HiveForm from '../components/HiveForm'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import Alert from '../../../components/feedback/Alert'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import { useHives } from '../hooks/useHives'

export const HiveDetailsPage = () => {
  const { id } = useParams()
  const location = useLocation()
  const [isEditing, setIsEditing] = useState(location.state?.edit === true)
  const [updateSuccess, setUpdateSuccess] = useState(false)

  const { selectedHive: hive, loading, error, fetchHive, updateHive, updateHiveStatus, clearError } = useHives()

  useEffect(() => {
    fetchHive(id)
  }, [id])

  const handleUpdate = async (formData) => {
    const result = await updateHive(id, formData)
    if (!result.error) {
      setIsEditing(false)
      setUpdateSuccess(true)
      setTimeout(() => setUpdateSuccess(false), 3000)
    }
  }

  const handleStatusToggle = async () => {
    if (!hive) return
    const newStatus = hive.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
    await updateHiveStatus(id, newStatus)
  }

  return (
    <BeekeeperLayout>
      <div className="w-full space-y-6">
        {/* Breadcrumb */}
        <nav className="flex items-center gap-2 text-xs text-slate-500">
          <Link to="/beekeeper/hives" className="hover:text-blue-600 transition-colors font-medium">My Hives</Link>
          <span>/</span>
          <span className="text-slate-800 font-mono font-semibold">{hive?.hiveCode || 'Loading...'}</span>
        </nav>

        {error && <Alert type="error" message={error} onClose={clearError} />}
        {updateSuccess && <Alert type="success" message="Hive updated successfully!" />}

        {loading && !hive ? (
          <LoadingSpinner text="Loading hive details..." />
        ) : !hive ? (
          <Card className="text-center py-16 max-w-xl mx-auto">
            <div className="text-5xl mb-4">🐝</div>
            <h2 className="text-xl font-bold text-slate-900 font-['Outfit']">Hive Not Found</h2>
            <p className="text-slate-500 text-sm mt-2">This hive doesn't exist or you don't have access.</p>
            <Link to="/beekeeper/hives">
              <Button variant="secondary" className="mt-4">← Back to Hives</Button>
            </Link>
          </Card>
        ) : (
          <div className="space-y-6">
            {/* Hive Header Card */}
            <Card className="p-6 bg-white border border-slate-200/90 shadow-sm">
              <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                <div className="flex items-center gap-4">
                  <div className="w-14 h-14 rounded-2xl bg-amber-100 border border-amber-200 flex items-center justify-center text-3xl shadow-sm">
                    🐝
                  </div>
                  <div>
                    <p className="text-amber-800 font-mono font-bold text-lg">{hive.hiveCode}</p>
                    <p className="text-slate-900 font-bold text-xl font-['Outfit']">{hive.clusterName}</p>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <HiveStatusBadge status={hive.status} size="lg" />
                  {!isEditing && (
                    <Button variant="primary" size="sm" onClick={() => setIsEditing(true)} className="bg-blue-600 hover:bg-blue-700 border-blue-600 text-white font-bold">
                      ✏️ Edit
                    </Button>
                  )}
                </div>
              </div>
            </Card>

            <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
              {/* Left Main Column */}
              <div className="lg:col-span-8 space-y-6">
                {isEditing ? (
                  /* Edit Form */
                  <Card className="p-6 bg-white border border-slate-200 shadow-sm">
                    <h2 className="text-lg font-bold text-slate-900 font-['Outfit'] mb-5">Edit Hive Details</h2>
                    <HiveForm
                      initialValues={hive}
                      onSubmit={handleUpdate}
                      loading={loading}
                      onCancel={() => setIsEditing(false)}
                      submitLabel="Save Changes"
                    />
                  </Card>
                ) : (
                  /* Details View */
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    {[
                      { label: 'Hive Code', value: hive.hiveCode, mono: true },
                      { label: 'Status', value: <HiveStatusBadge status={hive.status} /> },
                      { label: 'Installation Date', value: hive.installedDate
                        ? new Date(hive.installedDate).toLocaleDateString('en-IN', { day: '2-digit', month: 'long', year: 'numeric' })
                        : '—' },
                      { label: 'Coordinates', value: hive.latitude
                        ? `${hive.latitude}°N, ${hive.longitude}°E`
                        : 'Not specified', mono: !!hive.latitude },
                      { label: 'Registered', value: hive.createdAt
                        ? new Date(hive.createdAt).toLocaleDateString('en-IN')
                        : '—' },
                      { label: 'Last Updated', value: hive.updatedAt
                        ? new Date(hive.updatedAt).toLocaleDateString('en-IN')
                        : '—' },
                    ].map(({ label, value, mono }) => (
                      <div key={label} className="p-4 rounded-xl bg-white border border-slate-200/90 shadow-2xs">
                        <p className="text-xs text-slate-500 mb-1 font-medium">{label}</p>
                        <p className={`text-slate-900 font-semibold ${mono ? 'font-mono text-sm' : ''}`}>{value}</p>
                      </div>
                    ))}
                  </div>
                )}

                {/* Additional Modules */}
                <div className="space-y-3">
                  {['🌡️ Hive Health & Sensor Data', '📊 Yield Prediction', '🍯 Honey Batches'].map((section) => (
                    <div key={section} className="bg-white rounded-2xl p-5 border border-dashed border-slate-200 flex items-center justify-between shadow-2xs">
                      <span className="text-slate-900 font-semibold text-sm font-['Outfit']">{section}</span>
                      <span className="text-xs text-slate-500 bg-slate-100 px-3 py-1 rounded-full font-medium">
                        Live in telemetry tab
                      </span>
                    </div>
                  ))}
                </div>
              </div>

              {/* Right Sidebar Column */}
              <div className="lg:col-span-4 space-y-6">
                {/* Health Link Card */}
                <Card className="p-5 border border-amber-200/80 bg-gradient-to-br from-amber-50/50 to-white space-y-3">
                  <div className="flex items-center gap-2">
                    <span className="text-xl">📡</span>
                    <h3 className="font-bold text-slate-900 font-['Outfit'] text-sm">IoT Telemetry & Health</h3>
                  </div>
                  <p className="text-xs text-slate-600 leading-relaxed">
                    View real-time acoustic frequency, temperature, humidity, and colony weight telemetry.
                  </p>
                  <Link to={`/beekeeper/hives/${hive.id}/health`}>
                    <Button variant="primary" size="sm" className="w-full bg-blue-600 hover:bg-blue-700 border-blue-600 font-bold text-white mt-1">
                      View Hive Health →
                    </Button>
                  </Link>
                </Card>

                {/* Status Control */}
                {!isEditing && hive.status !== 'ALERT' && (
                  <Card className="p-5 space-y-3 bg-white border border-slate-200/90 shadow-sm">
                    <div>
                      <p className="text-slate-900 font-semibold text-sm">Hive Status Control</p>
                      <p className="text-slate-500 text-xs mt-1">
                        {hive.status === 'ACTIVE'
                          ? 'Deactivate this hive to mark it as not in operational use.'
                          : 'Activate this hive to mark it as operational.'}
                      </p>
                    </div>
                    <button
                      onClick={handleStatusToggle}
                      disabled={loading}
                      className={`w-full py-2 px-5 rounded-xl border text-sm font-medium transition-colors ${
                        hive.status === 'ACTIVE'
                          ? 'border-slate-300 text-slate-700 hover:bg-slate-100'
                          : 'border-blue-200 text-blue-700 hover:bg-blue-50'
                      }`}
                    >
                      {hive.status === 'ACTIVE' ? 'Deactivate Hive' : 'Activate Hive'}
                    </button>
                  </Card>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </BeekeeperLayout>
  )
}

export default HiveDetailsPage
