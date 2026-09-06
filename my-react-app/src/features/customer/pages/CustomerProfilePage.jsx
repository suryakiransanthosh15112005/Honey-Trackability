import React, { useState, useEffect } from 'react'
import CustomerLayout from '../../../layouts/CustomerLayout'
import Card from '../../../components/ui/Card'
import PageHeader from '../../../components/layout/PageHeader'
import customerApi from '../api/customerApi'
import { useLanguage } from '../../../i18n/LanguageContext'

export const CustomerProfilePage = () => {
  const { t } = useLanguage()
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [profileExists, setProfileExists] = useState(false)
  const [error, setError] = useState(null)
  const [successMsg, setSuccessMsg] = useState('')

  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    address: '',
    city: '',
    state: '',
    pincode: '',
  })

  useEffect(() => {
    fetchProfile()
  }, [])

  const fetchProfile = async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await customerApi.getProfile()
      const data = res.data?.data
      if (data && data.fullName) {
        setProfileExists(true)
        setFormData({
          fullName: data.fullName || '',
          email: data.email || '',
          address: data.address || '',
          city: data.city || '',
          state: data.state || '',
          pincode: data.pincode || '',
        })
      }
    } catch (err) {
      setError(err.response?.data?.message || t('common.errorLoading', 'Failed to load profile'))
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    setError(null)
    setSuccessMsg('')
    try {
      if (profileExists) {
        await customerApi.updateProfile(formData)
        setSuccessMsg(t('profile.updateSuccess', 'Profile updated successfully!'))
      } else {
        await customerApi.createProfile(formData)
        setProfileExists(true)
        setSuccessMsg(t('profile.createSuccess', 'Profile created successfully!'))
      }
    } catch (err) {
      setError(err.response?.data?.message || t('common.errorSaving', 'Failed to save profile details.'))
    } finally {
      setSaving(false)
    }
  }

  return (
    <CustomerLayout>
      <div className="w-full space-y-6">
        <PageHeader
          title={<span>{t('profile.customerTitle', 'Customer Profile')}</span>}
          subtitle={<span>{t('profile.customerSubtitle', 'Manage your personal details and delivery addresses.')}</span>}
        />

        {successMsg && (
          <div className="p-4 bg-blue-50 border border-blue-200 text-blue-800 rounded-xl flex items-center justify-between">
            <span>✅ {successMsg}</span>
            <button onClick={() => setSuccessMsg('')} className="text-blue-600 hover:text-blue-800 font-bold">✕</button>
          </div>
        )}

        {error && (
          <div className="p-4 bg-amber-50 border border-amber-200 text-amber-900 rounded-xl flex items-center justify-between">
            <span>⚠️ {error}</span>
            <button onClick={() => setError(null)} className="text-amber-700 hover:text-amber-900 font-bold">✕</button>
          </div>
        )}

        {loading ? (
          <Card className="p-12 text-center text-slate-500 max-w-xl mx-auto">
            <div className="animate-spin inline-block w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full mb-3" />
            <p>{t('loading.profile', 'Loading customer profile...')}</p>
          </Card>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
            {/* Main Profile Form */}
            <div className="lg:col-span-8">
              <Card className="p-6 md:p-8 space-y-6 bg-white border border-slate-200/90 shadow-sm">
                <form onSubmit={handleSubmit} className="space-y-5">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    <div>
                      <label className="block text-sm font-semibold text-slate-700 mb-1">
                        {t('profile.fullName', 'Full Name')} <span className="text-amber-600">*</span>
                      </label>
                      <input
                        type="text"
                        name="fullName"
                        required
                        value={formData.fullName}
                        onChange={handleChange}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                        placeholder="e.g. Ananya Sharma"
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-semibold text-slate-700 mb-1">
                        {t('profile.email', 'Email Address')}
                      </label>
                      <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                        placeholder="e.g. ananya@example.com"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-1">
                      {t('profile.address', 'Delivery Address')}
                    </label>
                    <textarea
                      name="address"
                      rows={3}
                      value={formData.address}
                      onChange={handleChange}
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                      placeholder="Door No, Street Name, Landmark..."
                    />
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
                    <div>
                      <label className="block text-sm font-semibold text-slate-700 mb-1">
                        {t('profile.city', 'City')}
                      </label>
                      <input
                        type="text"
                        name="city"
                        value={formData.city}
                        onChange={handleChange}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                        placeholder="e.g. Chennai"
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-semibold text-slate-700 mb-1">
                        {t('profile.state', 'State')}
                      </label>
                      <input
                        type="text"
                        name="state"
                        value={formData.state}
                        onChange={handleChange}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                        placeholder="e.g. Tamil Nadu"
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-semibold text-slate-700 mb-1">
                        {t('profile.pincode', 'Pincode')}
                      </label>
                      <input
                        type="text"
                        name="pincode"
                        value={formData.pincode}
                        onChange={handleChange}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                        placeholder="e.g. 600001"
                      />
                    </div>
                  </div>

                  <div className="pt-4 flex justify-end">
                    <button
                      type="submit"
                      disabled={saving}
                      className="btn btn--primary px-6 py-2.5 rounded-xl font-semibold text-white bg-blue-600 hover:bg-blue-700 disabled:opacity-50"
                    >
                      {saving ? t('common.saving', 'Saving...') : profileExists ? t('common.update', 'Update Profile') : t('common.create', 'Save Profile')}
                    </button>
                  </div>
                </form>
              </Card>
            </div>

            {/* Sidebar Account Summary */}
            <div className="lg:col-span-4 space-y-6">
              <Card className="p-6 bg-white border border-slate-200/90 shadow-sm space-y-4 text-center">
                <div className="w-20 h-20 rounded-full bg-gradient-to-tr from-amber-400 to-amber-500 text-white font-bold text-2xl flex items-center justify-center mx-auto shadow-md">
                  {formData.fullName ? formData.fullName.charAt(0).toUpperCase() : '👤'}
                </div>
                <div>
                  <h3 className="text-lg font-bold text-slate-900 font-['Outfit']">
                    {formData.fullName || 'Customer Profile'}
                  </h3>
                  <p className="text-xs text-slate-500 mt-0.5">
                    {formData.email || 'Verified Buyer'}
                  </p>
                </div>
                <div className="pt-2 border-t border-slate-100 flex items-center justify-between text-xs text-slate-600">
                  <span>Account Type:</span>
                  <span className="font-semibold text-blue-600">Verified Consumer</span>
                </div>
              </Card>

              <Card className="p-5 border border-amber-200/70 bg-gradient-to-br from-amber-50/50 to-white space-y-2">
                <div className="flex items-center gap-2">
                  <span className="text-lg">🚚</span>
                  <h4 className="font-bold text-slate-900 text-xs font-['Outfit']">Fast Doorstep Delivery</h4>
                </div>
                <p className="text-xs text-slate-600 leading-relaxed">
                  Keeping your address updated ensures smooth, temperature-controlled delivery directly from regional beekeepers.
                </p>
              </Card>
            </div>
          </div>
        )}
      </div>
    </CustomerLayout>
  )
}

export default CustomerProfilePage
