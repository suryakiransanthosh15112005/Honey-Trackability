import React, { useRef, useState } from 'react'
import Button from '../../../components/ui/Button'

export const BatchPhotoInput = ({ file, onFileChange, currentPhotoUrl = null }) => {
  const inputRef = useRef(null)
  const [previewUrl, setPreviewUrl] = useState(null)

  const handleSelect = (e) => {
    const selected = e.target.files[0]
    if (!selected) return

    if (!['image/jpeg', 'image/png', 'image/webp'].includes(selected.type)) {
      alert('Only JPEG, PNG, or WEBP images are supported.')
      return
    }

    if (selected.size > 5 * 1024 * 1024) {
      alert('Photo must be less than 5MB.')
      return
    }

    onFileChange(selected)
    const reader = new FileReader()
    reader.onload = () => setPreviewUrl(reader.result)
    reader.readAsDataURL(selected)
  }

  const handleRemove = () => {
    onFileChange(null)
    setPreviewUrl(null)
    if (inputRef.current) inputRef.current.value = ''
  }

  const activeDisplay = previewUrl || currentPhotoUrl

  return (
    <div className="space-y-2">
      <label className="block text-sm font-medium text-slate-700">
        Batch Harvest Photo <span className="text-slate-500 text-xs">(Optional)</span>
      </label>

      <input
        ref={inputRef}
        type="file"
        accept="image/jpeg,image/png,image/webp"
        capture="environment"
        onChange={handleSelect}
        className="hidden"
        id="batch-photo-input"
      />

      {activeDisplay ? (
        <div className="relative rounded-2xl overflow-hidden border border-slate-200 bg-slate-50 p-2 flex items-center gap-4">
          <img
            src={activeDisplay}
            alt="Batch harvest preview"
            className="w-24 h-24 object-cover rounded-xl border border-slate-200"
          />
          <div className="space-y-2">
            <p className="text-slate-800 text-xs font-semibold">
              {file ? file.name : 'Current Harvest Photo'}
            </p>
            <div className="flex items-center gap-2">
              <Button
                type="button"
                size="sm"
                variant="secondary"
                onClick={() => inputRef.current?.click()}
              >
                🔄 Retake
              </Button>
              <button
                type="button"
                onClick={handleRemove}
                className="text-xs text-slate-600 hover:text-slate-900 py-1 px-2.5 rounded-lg border border-slate-300 hover:bg-slate-100 transition-colors"
              >
                Remove
              </button>
            </div>
          </div>
        </div>
      ) : (
        <button
          type="button"
          onClick={() => inputRef.current?.click()}
          className="w-full p-6 rounded-2xl border-2 border-dashed border-slate-300 hover:border-blue-500 bg-slate-50 hover:bg-blue-50/50 flex flex-col items-center justify-center gap-2 transition-all cursor-pointer group"
        >
          <span className="text-3xl group-hover:scale-110 transition-transform">📷</span>
          <span className="text-slate-800 text-sm font-semibold">Add Batch Photo</span>
          <span className="text-xs text-slate-500">
            Take photo with camera or choose from gallery (Max 5MB)
          </span>
        </button>
      )}
    </div>
  )
}

export default BatchPhotoInput
