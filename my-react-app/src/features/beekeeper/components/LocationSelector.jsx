import React, { useState } from 'react'
import Input from '../../../components/ui/Input'
import Button from '../../../components/ui/Button'

export const LocationSelector = ({
  village,
  latitude,
  longitude,
  onVillageChange,
  onLocationChange,
  errors = {},
}) => {
  const [locating, setLocating] = useState(false)
  const [gpsError, setGpsError] = useState(null)
  const [gpsSuccess, setGpsSuccess] = useState(false)

  const handleGetGps = () => {
    if (!navigator.geolocation) {
      setGpsError('Geolocation is not supported by your browser')
      return
    }

    setLocating(true)
    setGpsError(null)

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const lat = parseFloat(position.coords.latitude.toFixed(4))
        const lng = parseFloat(position.coords.longitude.toFixed(4))
        onLocationChange(lat, lng)
        setLocating(false)
        setGpsSuccess(true)
      },
      (err) => {
        setLocating(false)
        setGpsError('GPS permission denied or unavailable. Please type your village name below.')
      },
      { timeout: 10000, enableHighAccuracy: true }
    )
  }

  return (
    <div className="space-y-4">
      {/* GPS Detection Button */}
      <div className="p-4 rounded-2xl bg-amber-50 border border-amber-200 shadow-sm">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
          <div>
            <p className="text-slate-900 font-semibold text-sm flex items-center gap-2">
              <span>📍</span> Auto-Detect GPS Location
            </p>
            <p className="text-slate-500 text-xs mt-0.5">
              One-click precise coordinates from your device
            </p>
          </div>
          <Button
            variant={gpsSuccess ? 'secondary' : 'primary'}
            size="sm"
            onClick={handleGetGps}
            loading={locating}
            className="w-full sm:w-auto"
          >
            {gpsSuccess ? '✓ GPS Captured' : '📡 Get GPS'}
          </Button>
        </div>

        {gpsSuccess && latitude && longitude && (
          <div className="mt-3 text-xs text-blue-700 font-bold flex items-center gap-2 font-mono">
            <span>✓ Lat: {latitude}° N, Lng: {longitude}° E</span>
          </div>
        )}

        {gpsError && (
          <div className="mt-2 text-xs text-amber-800 font-semibold">
            {gpsError}
          </div>
        )}
      </div>

      {/* Manual Village Input */}
      <Input
        id="village-input"
        label="Village / Town / District *"
        value={village}
        onChange={(e) => onVillageChange(e.target.value)}
        placeholder="e.g. Kotagiri, Nilgiris, Tamil Nadu"
        error={errors.village}
        required
      />

      {/* Manual Coordinates Override */}
      <div className="grid grid-cols-2 gap-3 pt-1">
        <Input
          id="latitude-input"
          label="Latitude (Optional)"
          type="number"
          step="0.0001"
          value={latitude || ''}
          onChange={(e) => onLocationChange(e.target.value ? parseFloat(e.target.value) : null, longitude)}
          placeholder="e.g. 11.4200"
          error={errors.latitude}
        />
        <Input
          id="longitude-input"
          label="Longitude (Optional)"
          type="number"
          step="0.0001"
          value={longitude || ''}
          onChange={(e) => onLocationChange(latitude, e.target.value ? parseFloat(e.target.value) : null)}
          placeholder="e.g. 76.8800"
          error={errors.longitude}
        />
      </div>
    </div>
  )
}

export default LocationSelector
