import React, { useRef, useState } from 'react'
import Button from '../../../components/ui/Button'

export const CertificateUpload = ({ file, onFileChange, currentCertificateUrl }) => {
  const inputRef = useRef(null)
  const [fileName, setFileName] = useState(null)

  const handleSelect = (e) => {
    const selected = e.target.files[0]
    if (!selected) return

    const allowed = ['application/pdf', 'image/jpeg', 'image/png', 'image/webp']
    if (!allowed.includes(selected.type)) {
      alert('Only PDF, JPEG, PNG, or WEBP documents are supported.')
      return
    }

    if (selected.size > 5 * 1024 * 1024) {
      alert('Certificate file size must be less than 5MB.')
      return
    }

    onFileChange(selected)
    setFileName(selected.name)
  }

  const handleRemove = () => {
    onFileChange(null)
    setFileName(null)
    if (inputRef.current) inputRef.current.value = ''
  }

  return (
    <div className="space-y-2">
      <label className="block text-sm font-medium text-slate-800 font-semibold">
        Laboratory Certificate Document <span className="text-slate-500 text-xs font-normal">(PDF / Image, Max 5MB)</span>
      </label>

      <input
        ref={inputRef}
        type="file"
        accept="application/pdf,image/jpeg,image/png,image/webp"
        onChange={handleSelect}
        className="hidden"
        id="lab-certificate-input"
      />

      {fileName || currentCertificateUrl ? (
        <div className="rounded-2xl border border-blue-200 bg-blue-50/60 p-4 flex items-center justify-between gap-4 shadow-sm">
          <div className="flex items-center gap-3">
            <span className="text-3xl">📄</span>
            <div>
              <p className="text-slate-900 text-xs font-bold font-mono">
                {fileName || 'Laboratory Certificate Attached'}
              </p>
              <p className="text-slate-500 text-[11px]">Ready for blockchain recording</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Button
              type="button"
              size="sm"
              variant="secondary"
              onClick={() => inputRef.current?.click()}
            >
              Replace
            </Button>
            <button
              type="button"
              onClick={handleRemove}
              className="text-xs text-slate-600 hover:text-slate-900 py-1.5 px-3 rounded-xl border border-slate-200 hover:bg-slate-100 transition-colors font-medium"
            >
              Remove
            </button>
          </div>
        </div>
      ) : (
        <button
          type="button"
          onClick={() => inputRef.current?.click()}
          className="w-full p-6 rounded-2xl border-2 border-dashed border-slate-300 hover:border-blue-500 bg-slate-50 hover:bg-blue-50/50 flex flex-col items-center justify-center gap-2 transition-all cursor-pointer group shadow-sm"
        >
          <span className="text-3xl group-hover:scale-110 transition-transform">📄</span>
          <span className="text-slate-900 text-sm font-semibold">Upload Certificate PDF / Image</span>
          <span className="text-xs text-slate-500">
            Attach official lab test report or purity certification (Optional)
          </span>
        </button>
      )}
    </div>
  )
}

export default CertificateUpload
