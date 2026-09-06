import React, { useState } from 'react'
import PurityScoreInput from './PurityScoreInput'
import CertificateUpload from './CertificateUpload'
import Button from '../../../components/ui/Button'
import Card from '../../../components/ui/Card'

export const LabTestForm = ({ onSubmit, loading, batchId }) => {
  const [purityScore, setPurityScore] = useState(98)
  const [result, setResult] = useState('PURE')
  const [remarks, setRemarks] = useState('')
  const [certificateFile, setCertificateFile] = useState(null)
  const [showConfirm, setShowConfirm] = useState(false)
  const [errors, setErrors] = useState({})

  const validate = () => {
    const errs = {}
    if (purityScore === null || purityScore === undefined || isNaN(purityScore)) {
      errs.purityScore = 'Purity score is required'
    } else if (purityScore < 0 || purityScore > 100) {
      errs.purityScore = 'Purity score must be between 0 and 100'
    }
    if (!result) errs.result = 'Lab result status is required'
    setErrors(errs)
    return Object.keys(errs).length === 0
  }

  const handlePreSubmit = (e) => {
    e.preventDefault()
    if (!validate()) return
    setShowConfirm(true)
  }

  const handleConfirmedSubmit = () => {
    setShowConfirm(false)
    const formData = new FormData()
    formData.append('purityScore', purityScore)
    formData.append('result', result)
    if (remarks.trim()) formData.append('remarks', remarks.trim())
    if (certificateFile) formData.append('certificate', certificateFile)
    onSubmit(formData)
  }

  return (
    <form onSubmit={handlePreSubmit} className="space-y-6">
      {/* Purity Score */}
      <PurityScoreInput
        value={purityScore}
        onChange={setPurityScore}
        error={errors.purityScore}
      />

      {/* Result Status Selector */}
      <div className="space-y-2">
        <label className="block text-sm font-medium text-slate-800 font-semibold">
          Certification Result *
        </label>
        <div className="grid grid-cols-3 gap-3">
          {[
            {
              id: 'PURE',
              label: 'Pure Certified',
              desc: 'Acceptable purity',
              icon: '✅',
              activeColor: 'border-blue-500 bg-blue-50 text-blue-900 shadow-sm font-bold',
            },
            {
              id: 'UNDER_REVIEW',
              label: 'Under Review',
              desc: 'Needs examination',
              icon: '⚠️',
              activeColor: 'border-amber-500 bg-amber-50 text-amber-900 shadow-sm font-bold',
            },
            {
              id: 'FAILED',
              label: 'Test Failed',
              desc: 'Adulteration detected',
              icon: '❌',
              activeColor: 'border-slate-400 bg-slate-100 text-slate-900 shadow-sm font-bold',
            },
          ].map(({ id, label, desc, icon, activeColor }) => {
            const isSelected = result === id
            return (
              <button
                key={id}
                type="button"
                onClick={() => setResult(id)}
                className={`p-3.5 rounded-2xl border text-center transition-all ${
                  isSelected
                    ? activeColor
                    : 'border-slate-200 bg-white text-slate-600 hover:border-slate-300'
                }`}
              >
                <span className="text-2xl block mb-1">{icon}</span>
                <p className="font-bold text-xs">{label}</p>
                <p className="text-[10px] opacity-75 mt-0.5">{desc}</p>
              </button>
            )
          })}
        </div>
        {errors.result && <p className="text-blue-600 text-xs mt-1 font-semibold">{errors.result}</p>}
      </div>

      {/* Certificate Upload */}
      <CertificateUpload
        file={certificateFile}
        onFileChange={setCertificateFile}
      />

      {/* Remarks */}
      <div>
        <label className="block text-sm font-medium text-slate-800 font-semibold mb-2">
          Lab Remarks & Testing Notes
        </label>
        <textarea
          rows={3}
          value={remarks}
          onChange={(e) => setRemarks(e.target.value)}
          placeholder="e.g. Moisture: 17.2%, Sucrose: 1.5%, HMF: 12mg/kg. Complies with FSSAI standards."
          className="w-full px-4 py-3 rounded-xl bg-white border border-slate-200 text-slate-900 text-sm focus:outline-none focus:border-blue-600 transition-colors resize-none shadow-sm"
        />
      </div>

      {/* Submit Action */}
      <div className="pt-2">
        <Button
          type="submit"
          variant="primary"
          loading={loading}
          className="w-full py-3.5 text-base font-bold bg-blue-600 hover:bg-blue-700 border-blue-600 text-white"
        >
          {loading ? 'Submitting Lab Test...' : '🧪 Submit Lab Result'}
        </Button>
      </div>

      {/* Confirmation Modal */}
      {showConfirm && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4 z-50 animate-fade-in">
          <Card className="max-w-md w-full p-6 space-y-4 border border-slate-200 bg-white shadow-2xl">
            <div className="text-4xl text-center">🧪</div>
            <h3 className="text-xl font-bold text-slate-900 text-center font-['Outfit']">
              Confirm Lab Submission
            </h3>
            <p className="text-slate-600 text-sm text-center">
              Submit lab test for batch <span className="font-mono text-amber-700 font-bold">{batchId}</span> with result{' '}
              <span className="font-bold text-slate-900">{result}</span> ({purityScore}% purity)?
            </p>
            <p className="text-xs text-amber-800 text-center bg-amber-50 p-2.5 rounded-xl border border-amber-200">
              ⚠️ This will create an immutable <span className="font-mono">LAB_RESULT</span> blockchain record that cannot be edited.
            </p>
            <div className="flex items-center gap-3 pt-2">
              <Button
                type="button"
                variant="primary"
                onClick={handleConfirmedSubmit}
                className="flex-1 bg-blue-600 hover:bg-blue-700 border-blue-600 font-bold text-white"
              >
                Confirm & Record
              </Button>
              <Button
                type="button"
                variant="secondary"
                onClick={() => setShowConfirm(false)}
                className="flex-1"
              >
                Cancel
              </Button>
            </div>
          </Card>
        </div>
      )}
    </form>
  )
}

export default LabTestForm
