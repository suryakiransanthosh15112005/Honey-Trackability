import React, { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import MainLayout from '../../../layouts/MainLayout'
import productApi from '../api/productApi'
import cartApi from '../../cart/api/cartApi'
import { fetchCart } from '../../cart/cartSlice'
import PurityBadge from '../components/PurityBadge'
import VerifiedBadge from '../components/VerifiedBadge'
import Alert from '../../../components/feedback/Alert'
import ReviewList from '../../review/components/ReviewList'
import RatingStars from '../../review/components/RatingStars'
import useReviews from '../../review/hooks/useReviews'

const ProductDetailsPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const { isAuthenticated, role } = useSelector((state) => state.auth)

  const [product, setProduct] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [quantity, setQuantity] = useState(1.0)
  const [addingToCart, setAddingToCart] = useState(false)
  const [cartSuccess, setCartSuccess] = useState(false)
  const [cartError, setCartError] = useState(null)

  // Reviews (live from backend)
  const { reviews, loading: reviewsLoading, error: reviewsError,
    page: reviewPage, totalPages: reviewTotalPages,
    totalElements: totalReviews, setPage: setReviewPage } = useReviews(id)

  useEffect(() => {
    const loadProduct = async () => {
      setLoading(true)
      try {
        const res = await productApi.getProduct(id)
        const data = res.data?.data
        setProduct(data)
        if (data?.availableQuantityKg && Number(data.availableQuantityKg) < 1.0) {
          setQuantity(Number(data.availableQuantityKg))
        }
      } catch (err) {
        setError(err?.response?.data?.message || 'Failed to load product details')
      } finally {
        setLoading(false)
      }
    }
    loadProduct()
  }, [id])

  const handleQuantityChange = (delta) => {
    if (!product) return
    const max = Number(product.availableQuantityKg || 1)
    const newQty = Math.round((quantity + delta) * 10) / 10
    if (newQty >= 0.5 && newQty <= max) {
      setQuantity(newQty)
    }
  }

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    if (role !== 'CUSTOMER') {
      setCartError('Only registered Customer accounts can purchase products.')
      return
    }

    setAddingToCart(true)
    setCartSuccess(false)
    setCartError(null)

    try {
      await cartApi.addItem({
        productId: Number(id),
        quantityKg: quantity,
      })
      dispatch(fetchCart())
      setCartSuccess(true)
    } catch (err) {
      setCartError(err?.response?.data?.message || 'Failed to add item to cart')
    } finally {
      setAddingToCart(false)
    }
  }

  if (loading) {
    return (
      <MainLayout>
        <div className="container section">
          <div className="card product-details-skeleton">
            <div className="skeleton skeleton--image h-80 sm:h-96"></div>
            <div className="skeleton skeleton--title mt-4"></div>
            <div className="skeleton skeleton--text"></div>
          </div>
        </div>
      </MainLayout>
    )
  }

  if (error || !product) {
    return (
      <MainLayout>
        <div className="container section">
          <Alert type="danger" message={error || 'Product not found'} />
          <Link to="/marketplace" className="btn btn--secondary mt-4">
            ← Back to Marketplace
          </Link>
        </div>
      </MainLayout>
    )
  }

  const defaultImg =
    'https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=800&auto=format&fit=crop&q=80'

  const maxStock = Number(product.availableQuantityKg || 0)
  const isOutOfStock = maxStock <= 0
  const subtotal = Math.round(Number(product.pricePerKg) * quantity * 100) / 100

  return (
    <MainLayout>
      <div className="product-details-page section">
        <div className="container">
          <nav className="breadcrumb mb-6">
            <Link to="/">Home</Link> / <Link to="/marketplace">Marketplace</Link> /{' '}
            <span className="text-secondary">{product.productName}</span>
          </nav>

          <div className="product-details-layout">
            {/* Left: Product Image & Badges */}
            <div className="product-details__media">
              <div className="card product-details__image-card">
                <img
                  src={product.imageUrl || defaultImg}
                  alt={product.productName}
                  className="product-details__image"
                  onError={(e) => {
                    e.target.src = defaultImg
                  }}
                />
              </div>

              {/* Lab Testing & Blockchain Proof Cards */}
              <div className="card product-details__proof-card mt-4">
                <h4 className="proof-card__title">🔒 Authenticity & Verification</h4>
                <div className="proof-card__grid mt-3">
                  <div className="proof-card__item">
                    <span className="proof-card__label">Lab Tested Purity</span>
                    <div className="mt-1">
                      <PurityBadge score={product.purityScore} size="lg" />
                    </div>
                  </div>

                  <div className="proof-card__item">
                    <span className="proof-card__label">Blockchain Ledger</span>
                    <div className="mt-1">
                      <VerifiedBadge verified={product.verified} batchId={product.batchId} />
                    </div>
                  </div>
                </div>

                {product.batchId && (
                  <div className="mt-4">
                    <Link
                      to={`/verify/${product.batchId}`}
                      className="btn btn--outline btn--sm btn--full"
                      target="_blank"
                    >
                      🔗 Inspect Public Blockchain Certificate
                    </Link>
                  </div>
                )}
              </div>
            </div>

            {/* Right: Product Info & Purchase Form */}
            <div className="product-details__info">
              <div className="product-details__header">
                <div className="product-details__tags">
                  <span className="badge badge--gold">{product.flowerSource || 'Multiflora'}</span>
                  <span className="badge badge--dark">📍 {product.region || 'India'}</span>
                  {product.harvestDate && (
                    <span className="badge badge--dark">🗓️ Harvested {product.harvestDate}</span>
                  )}
                </div>

                <h1 className="product-details__title mt-3">{product.productName}</h1>

                <div className="product-details__pricing mt-4">
                  <span className="product-details__price">₹{Number(product.pricePerKg).toFixed(0)}</span>
                  <span className="product-details__price-unit">per kg</span>
                </div>
              </div>

              {/* Description */}
              <div className="product-details__desc mt-4">
                <p>{product.description || '100% natural, unadulterated artisan honey harvested directly from certified sustainable beekeepers.'}</p>
              </div>

              {/* Beekeeper Story Card */}
              {product.beekeeper && (
                <div className="card beekeeper-story-card mt-6">
                  <h4 className="beekeeper-story__heading">🧑‍🌾 Produced by</h4>
                  <div className="beekeeper-story__body mt-2">
                    {product.beekeeper.photoUrl ? (
                      <img
                        src={product.beekeeper.photoUrl}
                        alt={product.beekeeper.name}
                        className="beekeeper-story__avatar"
                      />
                    ) : (
                      <div className="beekeeper-story__avatar-placeholder">🐝</div>
                    )}
                    <div>
                      <h4 className="beekeeper-story__name">{product.beekeeper.name}</h4>
                      <p className="beekeeper-story__village">📍 {product.beekeeper.village}</p>
                      {product.beekeeper.kvicId && (
                        <span className="badge badge--secondary badge--xs mt-1">
                          KVIC ID: {product.beekeeper.kvicId}
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              )}

              {/* Purchase Card */}
              <div className="card purchase-card mt-6">
                <div className="purchase-card__stock-info">
                  <span className="text-secondary">Stock Availability:</span>
                  <strong className={isOutOfStock ? 'text-danger' : 'text-success'}>
                    {isOutOfStock ? 'Out of Stock' : `${maxStock} kg available`}
                  </strong>
                </div>

                {!isOutOfStock && (
                  <div className="purchase-card__quantity-row mt-4">
                    <label className="form-label" htmlFor="qty-selector">
                      Select Quantity (kg):
                    </label>
                    <div className="quantity-selector" id="qty-selector">
                      <button
                        type="button"
                        className="quantity-selector__btn"
                        onClick={() => handleQuantityChange(-0.5)}
                        disabled={quantity <= 0.5}
                      >
                        −
                      </button>
                      <span className="quantity-selector__val">{quantity.toFixed(1)} kg</span>
                      <button
                        type="button"
                        className="quantity-selector__btn"
                        onClick={() => handleQuantityChange(0.5)}
                        disabled={quantity >= maxStock}
                      >
                        +
                      </button>
                    </div>
                  </div>
                )}

                <div className="purchase-card__subtotal mt-4">
                  <span className="text-secondary">Subtotal:</span>
                  <span className="purchase-card__subtotal-val">₹{subtotal.toFixed(2)}</span>
                </div>

                {cartSuccess && (
                  <div className="alert alert--success mt-4">
                    <span className="alert__icon">✅</span>
                    <div className="alert__body">
                      <p className="alert__message">
                        Added <strong>{quantity.toFixed(1)} kg</strong> of {product.productName} to your cart!
                      </p>
                      <div className="mt-2">
                        <Link to="/cart" className="btn btn--primary btn--sm">
                          🛒 View Cart & Checkout
                        </Link>
                      </div>
                    </div>
                  </div>
                )}

                {cartError && (
                  <div className="alert alert--danger mt-4">
                    <span className="alert__icon">⚠️</span>
                    <div className="alert__body">
                      <p className="alert__message">{cartError}</p>
                    </div>
                  </div>
                )}

                <div className="purchase-card__actions mt-6">
                  <button
                    type="button"
                    className="btn btn--primary btn--full btn--lg"
                    disabled={isOutOfStock || addingToCart}
                    onClick={handleAddToCart}
                  >
                    {addingToCart ? 'Adding to Cart...' : isOutOfStock ? 'Out of Stock' : '🛒 Add to Cart'}
                  </button>
                </div>
              </div>
            </div>
          </div>

          {/* ── Reviews Section ─────────────────────────────────── */}
          <div className="mt-8">
            <ReviewList
              reviews={reviews}
              loading={reviewsLoading}
              error={reviewsError}
              page={reviewPage}
              totalPages={reviewTotalPages}
              totalElements={totalReviews}
              onPageChange={setReviewPage}
              averageRating={product?.rating ? Number(product.rating) : 0}
              totalReviews={product?.reviewCount ?? 0}
            />
          </div>
        </div>
      </div>
    </MainLayout>
  )
}

export default ProductDetailsPage
