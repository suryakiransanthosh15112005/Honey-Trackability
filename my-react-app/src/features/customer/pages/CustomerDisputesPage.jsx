import React, { useState, useEffect } from 'react'
import CustomerLayout from '../../../layouts/CustomerLayout'
import Card from '../../../components/ui/Card'
import PageHeader from '../../../components/layout/PageHeader'
import disputeApi from '../api/disputeApi'
import { useLanguage } from '../../../i18n/LanguageContext'
import Button from '../../../components/ui/Button'
import EmptyState from '../../../components/ui/EmptyState'
import Modal from '../../../components/ui/Modal'
import DataTable from '../../../components/ui/DataTable'

export const CustomerDisputesPage = () => {
  const { t } = useLanguage()
  const [disputes, setDisputes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [statusFilter, setStatusFilter] = useState('')
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [selectedDispute, setSelectedDispute] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const [successMsg, setSuccessMsg] = useState('')

  const [newDispute, setNewDispute] = useState({
    batchId: '',
    orderNumber: '',
    reason: '',
    description: '',
  })

  useEffect(() => {
    fetchDisputes()
  }, [])

  const fetchDisputes = async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await disputeApi.getMyDisputes()
      setDisputes(res.data?.data?.content || [])
    } catch (err) {
      setError(err.response?.data?.message || t('common.errorLoading', 'Failed to load disputes.'))
    } finally {
      setLoading(false)
    }
  }

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setNewDispute((prev) => ({ ...prev, [name]: value }))
  }

  const handleCreateSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    setError(null)
    setSuccessMsg('')
    try {
      await disputeApi.createDispute(newDispute)
      setSuccessMsg(t('disputes.createSuccess', 'Dispute submitted successfully.'))
      setShowCreateModal(false)
      setNewDispute({ batchId: '', orderNumber: '', reason: '', description: '' })
      fetchDisputes()
    } catch (err) {
      setError(err.response?.data?.message || t('common.errorSaving', 'Failed to submit dispute.'))
    } finally {
      setSubmitting(false)
    }
  }

  const filteredDisputes = statusFilter
    ? disputes.filter((d) => d.status === statusFilter)
    : disputes

  const getStatusBadge = (status) => {
    switch (status) {
      case 'OPEN':
        return <span className="badge badge--warning">OPEN</span>
      case 'INVESTIGATING':
        return <span className="badge badge--primary">INVESTIGATING</span>
      case 'RESOLVED':
        return <span className="badge badge--success">RESOLVED</span>
      case 'REJECTED':
      default:
        return <span className="badge badge--secondary">{status}</span>
    }
  }

  return (
    <CustomerLayout>
      <div className="space-y-6 max-w-7xl mx-auto">
        <PageHeader
          title={
            <span>
              ⚖️ {t('disputes.title', 'My Authenticity Disputes')}
            </span>
          }
          subtitle={t('disputes.subtitle', 'Track and file authenticity or quality concerns for your honey orders.')}
          actions={
            <Button
              onClick={() => setShowCreateModal(true)}
              variant="primary"
              size="sm"
            >
              + {t('disputes.newDispute', 'File New Dispute')}
            </Button>
          }
        />

        {successMsg && (
          <div className="p-4 bg-blue-50 border border-blue-200 text-blue-800 rounded-lg flex items-center justify-between">
            <span>✅ {successMsg}</span>
            <button onClick={() => setSuccessMsg('')} className="text-blue-600 hover:text-blue-800 font-bold">✕</button>
          </div>
        )}

        {error && (
          <div className="p-4 bg-amber-50 border border-amber-200 text-amber-900 rounded-lg flex items-center justify-between">
            <span>⚠️ {error}</span>
            <button onClick={() => setError(null)} className="text-amber-700 hover:text-amber-900 font-bold">✕</button>
          </div>
        )}

        {/* Filter bar */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
          <div className="flex items-center gap-2">
            <span className="text-sm font-semibold text-slate-700">{t('disputes.filterStatus', 'Filter Status:')}</span>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="form-input py-1 px-3 text-sm w-auto"
            >
              <option value="">{t('disputes.allStatuses', 'All Statuses')}</option>
              <option value="OPEN">OPEN</option>
              <option value="INVESTIGATING">INVESTIGATING</option>
              <option value="RESOLVED">RESOLVED</option>
              <option value="REJECTED">REJECTED</option>
            </select>
          </div>
          <span className="text-xs text-slate-500 font-mono">
            {filteredDisputes.length} {t('disputes.recordsFound', 'record(s)')}
          </span>
        </div>

        {/* Disputes List / Table */}
        {loading ? (
          <Card className="p-12 text-center text-slate-500">
            <div className="animate-spin inline-block w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full mb-3" />
            <p>{t('loading.disputes', 'Loading disputes...')}</p>
          </Card>
        ) : filteredDisputes.length === 0 ? (
          <EmptyState
            icon="⚖️"
            title={t('disputes.emptyTitle', 'No disputes found')}
            description={t('disputes.emptyDesc', 'You have not submitted any disputes. Click "File New Dispute" if you suspect honey tampering or quality issues.')}
            action={
              <Button onClick={() => setShowCreateModal(true)} variant="primary" size="sm">
                + {t('disputes.newDispute', 'File New Dispute')}
              </Button>
            }
          />
        ) : (
          <DataTable>
            <thead>
              <tr className="bg-slate-50 border-b border-slate-200 text-slate-700 font-semibold">
                <th className="p-3">ID</th>
                <th className="p-3">Batch ID</th>
                <th className="p-3">Order Number</th>
                <th className="p-3">Reason</th>
                <th className="p-3">Status</th>
                <th className="p-3">Submitted</th>
                <th className="p-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filteredDisputes.map((dispute) => (
                <tr key={dispute.id} className="hover:bg-slate-50 transition-colors">
                  <td className="p-3 font-mono font-semibold text-slate-900">#{dispute.id}</td>
                  <td className="p-3 font-mono text-blue-600 font-medium">{dispute.batchId}</td>
                  <td className="p-3 font-mono text-slate-600">{dispute.orderNumber || 'N/A'}</td>
                  <td className="p-3 font-medium text-slate-800">{dispute.reason}</td>
                  <td className="p-3">{getStatusBadge(dispute.status)}</td>
                  <td className="p-3 text-slate-500 text-xs">
                    {dispute.createdAt ? new Date(dispute.createdAt).toLocaleDateString() : '—'}
                  </td>
                  <td className="p-3 text-right">
                    <Button
                      onClick={() => setSelectedDispute(dispute)}
                      variant="ghost"
                      size="xs"
                    >
                      {t('common.viewDetails', 'View')}
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </DataTable>
        )}

        {/* Modal: Dispute Details */}
        <Modal
          isOpen={!!selectedDispute}
          onClose={() => setSelectedDispute(null)}
          title={`⚖️ Dispute #${selectedDispute?.id || ''}`}
          footer={
            <Button onClick={() => setSelectedDispute(null)} variant="secondary" size="sm">
              {t('common.close', 'Close')}
            </Button>
          }
        >
          {selectedDispute && (
            <div className="space-y-3 text-sm">
              <div>
                <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">Batch ID</span>
                <span className="font-mono text-blue-600 font-bold">{selectedDispute.batchId}</span>
              </div>

              <div>
                <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">Order Number</span>
                <span className="font-mono text-slate-800">{selectedDispute.orderNumber || 'N/A'}</span>
              </div>

              <div>
                <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">Reason</span>
                <span className="text-slate-800 font-medium">{selectedDispute.reason}</span>
              </div>

              {selectedDispute.description && (
                <div>
                  <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">Description</span>
                  <p className="text-slate-700 bg-slate-50 p-2.5 rounded border text-xs leading-relaxed">
                    {selectedDispute.description}
                  </p>
                </div>
              )}

              <div>
                <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">Status</span>
                <div className="mt-1">{getStatusBadge(selectedDispute.status)}</div>
              </div>

              {selectedDispute.resolutionNotes && (
                <div>
                  <span className="text-xs font-semibold text-blue-700 uppercase tracking-wider block">Official Resolution Notes</span>
                  <p className="text-blue-900 bg-blue-50 p-2.5 rounded border border-blue-200 text-xs leading-relaxed font-medium">
                    {selectedDispute.resolutionNotes}
                  </p>
                </div>
              )}
            </div>
          )}
        </Modal>

        {/* Modal: File New Dispute */}
        <Modal
          isOpen={showCreateModal}
          onClose={() => setShowCreateModal(false)}
          title={`⚖️ ${t('disputes.fileTitle', 'File Authenticity Dispute')}`}
        >
          <form onSubmit={handleCreateSubmit} className="space-y-4 text-sm">
            <div>
              <label className="form-label">
                {t('disputes.batchIdLabel', 'Batch ID')} <span className="text-amber-700">*</span>
              </label>
              <input
                type="text"
                name="batchId"
                required
                value={newDispute.batchId}
                onChange={handleInputChange}
                placeholder="e.g. HC-2026-AB12CD34"
                className="form-input font-mono"
              />
            </div>

            <div>
              <label className="form-label">
                {t('disputes.orderNumberLabel', 'Order Number (Optional)')}
              </label>
              <input
                type="text"
                name="orderNumber"
                value={newDispute.orderNumber}
                onChange={handleInputChange}
                placeholder="e.g. ORD-20260904-XXXX"
                className="form-input font-mono"
              />
            </div>

            <div>
              <label className="form-label">
                {t('disputes.reasonLabel', 'Reason / Category')} <span className="text-amber-700">*</span>
              </label>
              <select
                name="reason"
                required
                value={newDispute.reason}
                onChange={handleInputChange}
                className="form-input"
              >
                <option value="">{t('disputes.selectReason', 'Select a reason...')}</option>
                <option value="Suspected Sugar Syrup Adulteration">Suspected Sugar Syrup Adulteration</option>
                <option value="QR Code Scan Verification Failed">QR Code Scan Verification Failed</option>
                <option value="Damaged or Tampered Seal">Damaged or Tampered Seal</option>
                <option value="Incorrect Batch / Labeling Mismatch">Incorrect Batch / Labeling Mismatch</option>
                <option value="Other Authenticity Concern">Other Authenticity Concern</option>
              </select>
            </div>

            <div>
              <label className="form-label">
                {t('disputes.descLabel', 'Detailed Description')}
              </label>
              <textarea
                name="description"
                rows={3}
                value={newDispute.description}
                onChange={handleInputChange}
                placeholder="Describe your observations, purchase details, or QR scan result..."
                className="form-input"
              />
            </div>

            <div className="pt-3 border-t border-slate-100 flex justify-end gap-3">
              <Button
                type="button"
                variant="ghost"
                size="sm"
                onClick={() => setShowCreateModal(false)}
              >
                {t('common.cancel', 'Cancel')}
              </Button>
              <Button
                type="submit"
                variant="primary"
                size="sm"
                loading={submitting}
              >
                {t('common.submit', 'Submit Dispute')}
              </Button>
            </div>
          </form>
        </Modal>
      </div>
    </CustomerLayout>
  )
}

export default CustomerDisputesPage
