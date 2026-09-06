import React, { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import productApi from '../api/productApi'
import { FLOWER_SOURCES, HONEY_REGIONS } from '../constants/marketplaceConstants'
import Alert from '../../../components/feedback/Alert'

const CreateProductPage = () => {
  const navigate = useNavigate()
  const [batches, setBatches] = useState([])
  const [loadingBatches, setLoadingBatches] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)

  const [formData, setFormData] = useState({
    batchId: '',
    productName: '',
    flowerSource: 'MULTIFLORA',
    region: 'Nilgiris, Tamil Nadu',
    pricePerKg: '',
    availableQuantityKg: '',
    description: '',
    imageUrl: '',
  })

  useEffect(() => {
    const loadBatches = async () => {
      setLoadingBatches(true)
      try {
        const res = await productApi.getEligibleBatches()
        const data = res.data?.data
        const eligibleList = data?.content || (Array.isArray(data) ? data : [])
        setBatches(eligibleList)
        if (eligibleList.length > 0) {
          const first = eligibleList[0]
          setFormData((prev) => ({
            ...prev,
            batchId: first.batchId,
            availableQuantityKg: first.quantityKg || '',
            productName: `${first.batchId} Pure Raw Honey`,
          }))
        }
      } catch (err) {
        setError(err?.response?.data?.message || 'Failed to load eligible batches')
      } finally {
        setLoadingBatches(false)
      }
    }
    loadBatches()
  }, [])

  const handleBatchSelect = (batchId) => {
    const selected = batches.find((b) => b.batchId === batchId)
    setFormData((prev) => ({
      ...prev,
      batchId,
      availableQuantityKg: selected?.quantityKg || '',
      productName: selected ? `${selected.batchId} Pure Raw Honey` : prev.productName,
    }))
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)

    if (!formData.batchId) {
      setError('Please select an eligible PURE honey batch.')
      return
    }

    if (!formData.productName.trim()) {
      setError('Product title is required.')
      return
    }

    if (!formData.pricePerKg || Number(formData.pricePerKg) <= 0) {
      setError('Price per kg must be greater than zero.')
      return
    }

    if (!formData.availableQuantityKg || Number(formData.availableQuantityKg) <= 0) {
      setError('Available quantity must be greater than zero.')
      return
    }

    setSubmitting(true)
    try {
      await productApi.createProduct({
        batchId: formData.batchId,
        productName: formData.productName.trim(),
        flowerSource: formData.flowerSource,
        region: formData.region,
        pricePerKg: Number(formData.pricePerKg),
        availableQuantityKg: Number(formData.availableQuantityKg),
        description: formData.description.trim(),
        imageUrl: formData.imageUrl.trim() || null,
      })
      navigate('/beekeeper/products')
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to create product listing')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <BeekeeperLayout>
      <div className="create-product-page section w-full">
        <div className="container w-full">
          <nav className="breadcrumb mb-4">
            <Link to="/beekeeper/dashboard">Dashboard</Link> /{' '}
            <Link to="/beekeeper/products">Listings</Link> /{' '}
            <span className="text-secondary">New Listing</span>
          </nav>

          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
            {/* Main Form Column */}
            <div className="lg:col-span-8">
              <div className="card">
                <div className="card__header">
                  <h1 className="card__title">🍯 Create New Honey Listing</h1>
                  <p className="card__subtitle">
                    List verified PURE honey batches directly on the HoneyChain marketplace.
                  </p>
                </div>

                {error && <Alert type="danger" message={error} className="mb-4" />}

                {loadingBatches ? (
                  <div className="p-6 text-center">Loading eligible honey batches...</div>
                ) : batches.length === 0 ? (
                  <div className="empty-state p-6 text-center">
                    <p className="text-secondary">
                      No eligible batches available for listing. Only batches with <strong>PURE</strong>,{' '}
                      <strong>QR_GENERATED</strong>, or <strong>IN_STOCK</strong> status can be listed.
                    </p>
                    <div className="mt-4">
                      <Link to="/beekeeper/batches/new" className="btn btn--secondary">
                        Create New Batch
                      </Link>
                    </div>
                  </div>
                ) : (
                  <form onSubmit={handleSubmit} className="form-grid">
                    {/* Eligible Batch Selector */}
                    <div className="form-group form-group--full">
                      <label className="form-label" htmlFor="batchId">
                        Select Verified Honey Batch <span className="text-danger">*</span>
                      </label>
                      <select
                        id="batchId"
                        name="batchId"
                        className="form-select"
                        value={formData.batchId}
                        onChange={(e) => handleBatchSelect(e.target.value)}
                        required
                      >
                        {batches.map((b) => (
                          <option key={b.batchId} value={b.batchId}>
                            {b.batchId} — {b.quantityKg} kg ({b.status})
                          </option>
                        ))}
                      </select>
                    </div>

                    {/* Product Name */}
                    <div className="form-group form-group--full">
                      <label className="form-label" htmlFor="productName">
                        Product Listing Title <span className="text-danger">*</span>
                      </label>
                      <input
                        id="productName"
                        name="productName"
                        type="text"
                        className="form-input"
                        placeholder="e.g. Pure Wildflower Honey - Nilgiris"
                        value={formData.productName}
                        onChange={handleChange}
                        maxLength={120}
                        required
                      />
                    </div>

                    {/* Flower Source */}
                    <div className="form-group">
                      <label className="form-label" htmlFor="flowerSource">
                        Flower / Floral Source
                      </label>
                      <select
                        id="flowerSource"
                        name="flowerSource"
                        className="form-select"
                        value={formData.flowerSource}
                        onChange={handleChange}
                      >
                        {FLOWER_SOURCES.filter((f) => f.value).map((f) => (
                          <option key={f.value} value={f.value}>
                            {f.label}
                          </option>
                        ))}
                      </select>
                    </div>

                    {/* Region */}
                    <div className="form-group">
                      <label className="form-label" htmlFor="region">
                        Harvest Region
                      </label>
                      <select
                        id="region"
                        name="region"
                        className="form-select"
                        value={formData.region}
                        onChange={handleChange}
                      >
                        {HONEY_REGIONS.filter((r) => r.value).map((r) => (
                          <option key={r.value} value={r.value}>
                            {r.label}
                          </option>
                        ))}
                      </select>
                    </div>

                    {/* Price Per KG */}
                    <div className="form-group">
                      <label className="form-label" htmlFor="pricePerKg">
                        Price per KG (₹) <span className="text-danger">*</span>
                      </label>
                      <input
                        id="pricePerKg"
                        name="pricePerKg"
                        type="number"
                        step="1"
                        min="1"
                        className="form-input"
                        placeholder="e.g. 850"
                        value={formData.pricePerKg}
                        onChange={handleChange}
                        required
                      />
                    </div>

                    {/* Available Quantity */}
                    <div className="form-group">
                      <label className="form-label" htmlFor="availableQuantityKg">
                        Listing Quantity (KG) <span className="text-danger">*</span>
                      </label>
                      <input
                        id="availableQuantityKg"
                        name="availableQuantityKg"
                        type="number"
                        step="0.1"
                        min="0.1"
                        className="form-input"
                        placeholder="e.g. 8.5"
                        value={formData.availableQuantityKg}
                        onChange={handleChange}
                        required
                      />
                    </div>

                    {/* Image URL */}
                    <div className="form-group form-group--full">
                      <label className="form-label" htmlFor="imageUrl">
                        Product Image URL (optional)
                      </label>
                      <input
                        id="imageUrl"
                        name="imageUrl"
                        type="url"
                        className="form-input"
                        placeholder="https://images.unsplash.com/..."
                        value={formData.imageUrl}
                        onChange={handleChange}
                      />
                    </div>

                    {/* Description */}
                    <div className="form-group form-group--full">
                      <label className="form-label" htmlFor="description">
                        Description & Tasting Notes
                      </label>
                      <textarea
                        id="description"
                        name="description"
                        rows={4}
                        className="form-textarea"
                        placeholder="Describe the aroma, flavor profile, floral notes, and harvest story..."
                        value={formData.description}
                        onChange={handleChange}
                        maxLength={1500}
                      />
                    </div>

                    {/* Actions */}
                    <div className="form-group form-group--full flex justify-end gap-3 mt-4">
                      <Link to="/beekeeper/products" className="btn btn--secondary">
                        Cancel
                      </Link>
                      <button
                        type="submit"
                        className="btn btn--primary"
                        disabled={submitting}
                      >
                        {submitting ? 'Creating Listing...' : 'Publish Product to Marketplace'}
                      </button>
                    </div>
                  </form>
                )}
              </div>
            </div>

            {/* Sidebar Guidelines Column */}
            <div className="lg:col-span-4 space-y-6">
              <div className="card p-5 border border-amber-200 bg-gradient-to-br from-amber-50/60 to-white space-y-3">
                <div className="flex items-center gap-2">
                  <span className="text-xl">💰</span>
                  <h3 className="font-bold text-slate-900 font-['Outfit'] text-sm">Fair Beekeeper Pricing</h3>
                </div>
                <p className="text-xs text-slate-600 leading-relaxed">
                  On HoneyChain, you keep 100% of your listed retail price minus standard UPI payment processing. Fair floor prices reward verified pure beekeeping.
                </p>
              </div>

              <div className="card p-5 border border-blue-200 bg-gradient-to-br from-blue-50/40 to-white space-y-2">
                <div className="flex items-center gap-2">
                  <span className="text-xl">🛡️</span>
                  <h3 className="font-bold text-slate-900 font-['Outfit'] text-sm">Consumer Verification Badge</h3>
                </div>
                <p className="text-xs text-slate-600 leading-relaxed">
                  Your listing automatically displays lab purity certificates and QR scan passports to build instant consumer trust.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </BeekeeperLayout>
  )
}

export default CreateProductPage
