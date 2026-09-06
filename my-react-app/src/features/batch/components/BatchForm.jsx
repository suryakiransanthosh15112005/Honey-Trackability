import React, { useState, useEffect } from 'react'
import QuantityStepper from './QuantityStepper'
import BatchPhotoInput from './BatchPhotoInput'
import Button from '../../../components/ui/Button'

export const BatchForm = ({
  initialValues = {},
  hives = [],
  onSubmit,
  loading = false,
  isEdit = false,
  onCancel,
  submitLabel = 'Create Batch',
}) => {
  const [hiveId, setHiveId] = useState(initialValues.hiveId || (hives[0]?.id ?? ''))
  const [harvestDate, setHarvestDate] = useState(
    initialValues.harvestDate
      ? typeof initialValues.harvestDate === 'string'
        ? initialValues.harvestDate.substring(0, 10)
        : initialValues.harvestDate
      : new Date().toISOString().substring(0, 10)
  )
  const [quantityKg, setQuantityKg] = useState(initialValues.quantityKg ?? 5.0)
  const [photoFile, setPhotoFile] = useState(null)
  const [errors, setErrors] = useState({})

  useEffect(() => {
    if (!isEdit && !hiveId && hives.length > 0) {
      const firstActive = hives.find((h) => h.status === 'ACTIVE') || hives[0]
      if (firstActive) setHiveId(firstActive.id)
    }
  }, [hives, hiveId, isEdit])

  const validate = () => {
    const errs = {}
    if (!isEdit && !hiveId) errs.hiveId = 'Please select a hive'
    if (!harvestDate) errs.harvestDate = 'Harvest date is required'
    if (harvestDate && harvestDate > new Date().toISOString().substring(0, 10)) {
      errs.harvestDate = 'Harvest date cannot be in the future'
    }
    const q = parseFloat(quantityKg)
    if (isNaN(q) || q <= 0) {
      errs.quantityKg = 'Quantity must be greater than 0'
    } else if (q > 10000) {
      errs.quantityKg = 'Quantity cannot exceed 10,000 kg'
    }
    setErrors(errs)
    return Object.keys(errs).length === 0
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!validate()) return

    const formData = new FormData()
    if (!isEdit) {
      formData.append('hiveId', hiveId)
    }
    formData.append('harvestDate', harvestDate)
    formData.append('quantityKg', quantityKg)
    if (photoFile) {
      formData.append('photo', photoFile)
    }

    onSubmit(formData)
  }

  const activeHives = hives.filter((h) => h.status === 'ACTIVE')

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Hive Selector (Create Mode Only) */}
      {!isEdit && (
        <div className="space-y-2">
          <label className="block text-sm font-medium text-slate-700">
            Select Source Hive *
          </label>
          {activeHives.length === 0 ? (
            <div className="p-4 rounded-xl border border-amber-200 bg-amber-50 text-amber-900 text-sm">
              ⚠️ No active hives found. Please register and activate a hive before creating a batch.
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {activeHives.map((hive) => {
                const isSelected = String(hiveId) === String(hive.id)
                return (
                  <button
                    key={hive.id}
                    type="button"
                    onClick={() => setHiveId(hive.id)}
                    className={`p-4 rounded-2xl border text-left transition-all flex items-start gap-3 ${
                      isSelected
                        ? 'border-blue-600 bg-blue-50/70 shadow-sm'
                        : 'border-slate-200 bg-slate-50 hover:border-blue-300'
                    }`}
                  >
                    <span className="text-2xl mt-0.5">🐝</span>
                    <div className="flex-1">
                      <div className="flex items-center justify-between">
                        <span className="font-mono font-bold text-slate-900 text-sm">
                          {hive.hiveCode}
                        </span>
                        {isSelected && (
                          <span className="text-blue-600 text-xs font-bold">✓ Selected</span>
                        )}
                      </div>
                      <p className="text-xs text-slate-500 mt-0.5">{hive.clusterName}</p>
                    </div>
                  </button>
                )
              })}
            </div>
          )}
          {errors.hiveId && <p className="text-blue-600 text-xs mt-1">{errors.hiveId}</p>}
        </div>
      )}

      {/* Harvest Date */}
      <div>
        <label className="block text-sm font-medium text-slate-700 mb-2">
          Harvest Date *
        </label>
        <input
          id="batch-harvest-date"
          type="date"
          value={harvestDate}
          max={new Date().toISOString().substring(0, 10)}
          onChange={(e) => setHarvestDate(e.target.value)}
          className="w-full px-4 py-3 rounded-xl bg-slate-50 border border-slate-300 text-slate-900 text-sm focus:outline-none focus:border-blue-600 focus:bg-white transition-colors"
          required
        />
        {errors.harvestDate && <p className="text-blue-600 text-xs mt-1">{errors.harvestDate}</p>}
      </div>

      {/* Quantity Stepper */}
      <QuantityStepper
        value={quantityKg}
        onChange={setQuantityKg}
        error={errors.quantityKg}
      />

      {/* Photo Input */}
      <BatchPhotoInput
        file={photoFile}
        onFileChange={setPhotoFile}
        currentPhotoUrl={initialValues.photoUrl}
      />

      {/* Action Buttons */}
      <div className="flex items-center gap-3 pt-4">
        <Button
          type="submit"
          variant="primary"
          loading={loading}
          disabled={!isEdit && activeHives.length === 0}
          className="flex-1 py-3 text-base font-bold"
        >
          {loading ? 'Processing Batch...' : submitLabel}
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

export default BatchForm
