import React from 'react'
import Button from '../../../components/ui/Button'

export const CertificateViewer = ({ url, onClose }) => {
  if (!url) return null

  const isPdf = url.toLowerCase().endsWith('.pdf')

  return (
    <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-md flex items-center justify-center p-4 z-50 animate-fade-in">
      <div className="max-w-2xl w-full bg-white border border-slate-200 rounded-3xl p-6 space-y-4 shadow-2xl">
        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
          <div className="flex items-center gap-2">
            <span className="text-xl">📄</span>
            <h3 className="text-base font-bold text-slate-900 font-['Outfit']">
              Official Laboratory Certificate
            </h3>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="text-xs text-slate-500 hover:text-slate-900 px-2.5 py-1 rounded-lg bg-slate-100 border border-slate-200"
          >
            ✕ Close
          </button>
        </div>

        <div className="p-4 rounded-2xl bg-slate-50 border border-slate-200 flex flex-col items-center justify-center min-h-56">
          {isPdf ? (
            <div className="text-center space-y-3">
              <span className="text-5xl block">📑</span>
              <p className="text-sm font-semibold text-slate-900">Laboratory Certificate PDF Document</p>
              <p className="text-xs text-slate-600 max-w-xs">
                Official accredited purity analysis report. Click below to view or download.
              </p>
              <a
                href={url}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-block pt-2"
              >
                <Button variant="primary" size="sm" className="font-bold">
                  Open Full PDF Document ↗
                </Button>
              </a>
            </div>
          ) : (
            <img
              src={url}
              alt="Lab Certificate"
              className="max-h-96 w-auto rounded-xl object-contain border border-slate-200"
            />
          )}
        </div>

        <div className="flex items-center justify-between text-xs text-slate-500 pt-2">
          <span>Protected by HoneyChain Blockchain verification</span>
          <Button variant="secondary" size="sm" onClick={onClose}>
            Done
          </Button>
        </div>
      </div>
    </div>
  )
}

export default CertificateViewer
