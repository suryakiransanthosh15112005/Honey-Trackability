import React, { useState } from 'react'
import Input from '../../../components/ui/Input'
import Button from '../../../components/ui/Button'
import LocationSelector from './LocationSelector'
import LanguageSelector from './LanguageSelector'

export const ProfileForm = ({
  initialValues = {},
  onSubmit,
  loading = false,
  onCancel,
}) => {
  const [formData, setFormData] = useState({
    name: initialValues.name || '',
    village: initialValues.village || '',
    photoUrl: initialValues.photoUrl || '',
    latitude: initialValues.latitude || null,
    longitude: initialValues.longitude || null,
    preferredLanguage: initialValues.preferredLanguage || 'ENGLISH',
  })
  const [errors, setErrors] = useState({})

  const validate = () => {
    const errs = {}
    if (!formData.name.trim()) errs.name = 'Name is required'
    if (!formData.village.trim()) errs.village = 'Village is required'
    if (formData.latitude !== null && (formData.latitude < -90 || formData.latitude > 90)) {
      errs.latitude = 'Latitude must be between -90 and 90'
    }
    if (formData.longitude !== null && (formData.longitude < -180 || formData.longitude > 180)) {
      errs.longitude = 'Longitude must be between -180 and 180'
    }
    setErrors(errs)
    return Object.keys(errs).length === 0
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!validate()) return
    onSubmit(formData)
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <Input
        id="profile-name"
        label="Full Name *"
        value={formData.name}
        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
        placeholder="Enter your full name"
        error={errors.name}
        required
      />

      <LocationSelector
        village={formData.village}
        latitude={formData.latitude}
        longitude={formData.longitude}
        onVillageChange={(v) => setFormData({ ...formData, village: v })}
        onLocationChange={(lat, lng) => setFormData({ ...formData, latitude: lat, longitude: lng })}
        errors={errors}
      />

      <LanguageSelector
        selectedLanguage={formData.preferredLanguage}
        onChange={(lang) => setFormData({ ...formData, preferredLanguage: lang })}
      />

      <Input
        id="profile-photo"
        label="Photo URL (Optional)"
        value={formData.photoUrl}
        onChange={(e) => setFormData({ ...formData, photoUrl: e.target.value })}
        placeholder="https://..."
      />

      <div className="flex items-center gap-3 pt-4">
        <Button
          type="submit"
          variant="primary"
          loading={loading}
          className="flex-1 py-3"
        >
          Save Changes
        </Button>
        {onCancel && (
          <Button
            type="button"
            variant="secondary"
            onClick={onCancel}
            className="py-3 px-6"
          >
            Cancel
          </Button>
        )}
      </div>
    </form>
  )
}

export default ProfileForm
