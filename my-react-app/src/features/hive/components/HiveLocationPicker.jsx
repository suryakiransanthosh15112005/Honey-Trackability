import React, { useState } from 'react'
import Button from '../../../components/ui/Button'

export const HiveLocationPicker = ({ latitude, longitude, onLocationChange }) => {
  const [locating, setLocating] = useState(false)
  const [gpsError, setGpsError] = useState(null)
  const [gpsSuccess, setGpsSuccess] = useState(false)

  const handleGps = () => {
    if (!navigator.geolocation) {
      setGpsError('Geolocation not supported by your browser')
      return
    }
    setLocating(true)
    setGpsError(null)
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const lat = parseFloat(pos.coords.latitude.toFixed(4))
        const lng = parseFloat(pos.coords.longitude.toFixed(4))
        onLocationChange(lat, lng)
        setLocating(false)
        setGpsSuccess(true)
      },
      () => {
        setLocating(false)
        setGpsError('GPS unavailable. Enter coordinates manually or skip.')
      },
      { timeout: 10000, enableHighAccuracy: true }
    )
  }

  return (
    <div className="space-y-3">
      <label className="block text-sm font-semibold text-slate-800">
        Hive Location <span className="text-slate-500 text-xs font-normal">(Optional)</span>
      </label>

      <div className="p-4 rounded-xl bg-amber-50 border border-amber-200 flex flex-col sm:flex-row items-start sm:items-center gap-3 justify-between shadow-sm">
        <div>
          <p className="text-slate-900 text-sm font-medium flex items-center gap-2">
            <span>📍</span> Auto-Detect GPS
          </p>
          {gpsSuccess && latitude && (
            <p className="text-blue-700 text-xs mt-1 font-mono font-bold">
              ✓ {latitude}°N, {longitude}°E
            </p>
          )}
          {gpsError && <p className="text-amber-800 text-xs mt-1 font-semibold">{gpsError}</p>}
        </div>
        <Button
          variant={gpsSuccess ? 'secondary' : 'primary'}
          size="sm"
          onClick={handleGps}
          loading={locating}
          type="button"
          className="w-full sm:w-auto"
        >
          {gpsSuccess ? '✓ Captured' : '📡 Get GPS'}
        </Button>
      </div>

      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="text-xs text-slate-500 mb-1 block font-medium">Latitude</label>
          <input
            type="number"
            step="0.0001"
            value={latitude ?? ''}
            onChange={(e) => onLocationChange(e.target.value ? parseFloat(e.target.value) : null, longitude)}
            placeholder="e.g. 11.4200"
            className="w-full px-3 py-2 rounded-xl bg-white border border-slate-200 text-slate-900 text-sm placeholder:text-slate-400 focus:outline-none focus:border-blue-600 shadow-sm"
          />
        </div>
        <div>
          <label className="text-xs text-slate-500 mb-1 block font-medium">Longitude</label>
          <input
            type="number"
            step="0.0001"
            value={longitude ?? ''}
            onChange={(e) => onLocationChange(latitude, e.target.value ? parseFloat(e.target.value) : null)}
            placeholder="e.g. 76.8800"
            className="w-full px-3 py-2 rounded-xl bg-white border border-slate-200 text-slate-900 text-sm placeholder:text-slate-400 focus:outline-none focus:border-blue-600 shadow-sm"
          />
        </div>
      </div>
    </div>
  )
}

export default HiveLocationPicker
