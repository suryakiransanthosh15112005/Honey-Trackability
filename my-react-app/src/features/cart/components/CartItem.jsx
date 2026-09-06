import React from 'react'
import { Link } from 'react-router-dom'

const CartItem = ({ item, onUpdateQuantity, onRemove, disabled }) => {
  if (!item) return null

  const { id, productId, productName, imageUrl, quantityKg, unitPrice, subtotal, availableQuantityKg } = item

  const defaultImg =
    'https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=300&auto=format&fit=crop&q=60'

  const maxStock = Number(availableQuantityKg || 10)

  const handleStep = (delta) => {
    const nextQty = Math.round((Number(quantityKg) + delta) * 10) / 10
    if (nextQty >= 0.5 && nextQty <= maxStock) {
      onUpdateQuantity(id, nextQty)
    }
  }

  return (
    <div className="cart-item-row">
      <div className="cart-item__media">
        <img
          src={imageUrl || defaultImg}
          alt={productName}
          className="cart-item__img"
          onError={(e) => {
            e.target.src = defaultImg
          }}
        />
      </div>

      <div className="cart-item__details">
        <h4 className="cart-item__title">
          <Link to={`/marketplace/product/${productId}`}>{productName}</Link>
        </h4>
        <div className="cart-item__unit-price text-secondary text-sm">
          ₹{Number(unitPrice).toFixed(2)} per kg
        </div>
      </div>

      <div className="cart-item__quantity">
        <div className="quantity-selector quantity-selector--sm">
          <button
            type="button"
            className="quantity-selector__btn"
            onClick={() => handleStep(-0.5)}
            disabled={disabled || Number(quantityKg) <= 0.5}
          >
            −
          </button>
          <span className="quantity-selector__val">{Number(quantityKg).toFixed(1)} kg</span>
          <button
            type="button"
            className="quantity-selector__btn"
            onClick={() => handleStep(0.5)}
            disabled={disabled || Number(quantityKg) >= maxStock}
          >
            +
          </button>
        </div>
        <span className="text-xs text-muted mt-1 block">Max {maxStock} kg</span>
      </div>

      <div className="cart-item__subtotal">
        <span className="cart-item__subtotal-val">₹{Number(subtotal).toFixed(2)}</span>
      </div>

      <div className="cart-item__action">
        <button
          type="button"
          className="btn btn--ghost btn--xs text-danger"
          onClick={() => onRemove(id)}
          disabled={disabled}
          title="Remove from cart"
        >
          🗑️
        </button>
      </div>
    </div>
  )
}

export default CartItem
