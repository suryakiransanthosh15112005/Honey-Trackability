import React from 'react'

export const Button = ({
  children,
  variant = 'primary',
  size = 'md',
  type = 'button',
  disabled = false,
  loading = false,
  className = '',
  onClick,
  ...props
}) => {
  const variantClass = {
    primary: 'btn--primary',
    secondary: 'btn--secondary',
    ghost: 'btn--ghost',
    danger: 'btn--danger',
    success: 'btn--success',
  }[variant] || 'btn--primary'

  const sizeClass = {
    xs: 'btn--xs',
    sm: 'btn--sm',
    md: 'btn--md',
    lg: 'btn--lg',
  }[size] || 'btn--md'

  return (
    <button
      type={type}
      disabled={disabled || loading}
      onClick={onClick}
      className={`btn ${variantClass} ${sizeClass} ${className}`}
      {...props}
    >
      {loading ? (
        <span className="btn-spinner">
          <span className="btn-spinner__icon" />
          {children}
        </span>
      ) : (
        children
      )}
    </button>
  )
}

export default Button
