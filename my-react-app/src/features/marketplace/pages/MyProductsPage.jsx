import React, { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import productApi from '../api/productApi'
import PurityBadge from '../components/PurityBadge'
import VerifiedBadge from '../components/VerifiedBadge'
import Alert from '../../../components/feedback/Alert'

const MyProductsPage = () => {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [togglingId, setTogglingId] = useState(null)

  const loadMyProducts = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await productApi.getMyProducts({ page: 0, size: 50 })
      const data = res.data?.data
      if (data?.content) {
        setProducts(data.content)
      } else if (Array.isArray(data)) {
        setProducts(data)
      }
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to load product listings')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadMyProducts()
  }, [loadMyProducts])

  const handleToggleStatus = async (productId, currentActive) => {
    setTogglingId(productId)
    try {
      await productApi.setProductStatus(productId, !currentActive)
      setProducts((prev) =>
        prev.map((p) => (p.id === productId ? { ...p, isActive: !currentActive } : p))
      )
    } catch (err) {
      alert(err?.response?.data?.message || 'Failed to update listing status')
    } finally {
      setTogglingId(null)
    }
  }

  return (
    <BeekeeperLayout>
      <div className="beekeeper-products-page section">
        <div className="container">
          <div className="page-header mb-6">
            <div>
              <h1 className="page-header__title">🍯 My Honey Marketplace Listings</h1>
              <p className="page-header__subtitle">
                Manage your public product listings created from certified PURE honey batches.
              </p>
            </div>
            <Link to="/beekeeper/products/new" className="btn btn--primary">
              + Create New Listing
            </Link>
          </div>

          {error && <Alert type="danger" message={error} className="mb-6" />}

          {loading ? (
            <div className="card p-6 text-center">Loading your product listings...</div>
          ) : products.length === 0 ? (
            <div className="empty-state card p-8 text-center">
              <div className="empty-state__icon">🍯</div>
              <h3 className="empty-state__title">No Product Listings Yet</h3>
              <p className="empty-state__description">
                List your lab-tested, verified PURE honey batches to sell directly to conscious consumers.
              </p>
              <div className="mt-4">
                <Link to="/beekeeper/products/new" className="btn btn--primary">
                  + Create Your First Listing
                </Link>
              </div>
            </div>
          ) : (
            <div className="card overflow-x-auto">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Product</th>
                    <th>Batch ID</th>
                    <th>Price / kg</th>
                    <th>Available Stock</th>
                    <th>Verification</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {products.map((p) => (
                    <tr key={p.id}>
                      <td>
                        <div className="table-product-cell">
                          <strong>{p.productName}</strong>
                          <span className="text-secondary text-xs">{p.flowerSource} • {p.region}</span>
                        </div>
                      </td>
                      <td>
                        <code className="code-badge">{p.batchId}</code>
                      </td>
                      <td>
                        <strong>₹{Number(p.pricePerKg).toFixed(2)}</strong>
                      </td>
                      <td>
                        <span className={Number(p.availableQuantityKg) <= 0 ? 'text-danger' : 'text-success'}>
                          {p.availableQuantityKg} kg
                        </span>
                      </td>
                      <td>
                        <div className="flex gap-1 flex-wrap">
                          <VerifiedBadge verified={p.verified} batchId={p.batchId} />
                          {p.purityScore != null && <PurityBadge score={p.purityScore} size="sm" />}
                        </div>
                      </td>
                      <td>
                        <span className={`badge ${p.isActive ? 'badge--success' : 'badge--secondary'}`}>
                          {p.isActive ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td>
                        <button
                          type="button"
                          className={`btn btn--xs ${p.isActive ? 'btn--secondary' : 'btn--outline'}`}
                          disabled={togglingId === p.id}
                          onClick={() => handleToggleStatus(p.id, p.isActive)}
                        >
                          {togglingId === p.id ? 'Updating...' : p.isActive ? 'Deactivate' : 'Activate'}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </BeekeeperLayout>
  )
}

export default MyProductsPage
