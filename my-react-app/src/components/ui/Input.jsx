import React from 'react'

export const Input = ({
  label,
  id,
  type = 'text',
  error,
  helperText,
  className = '',
  ...props
}) => {
  return (
    <div className="form-group">
      {label && (
        <label htmlFor={id} className="form-label">
          {label}
        </label>
      )}
      <input
        id={id}
        type={type}
        className={`form-input${error ? ' form-input--error' : ''} ${className}`}
        {...props}
      />
      {error && <p className="form-error">{error}</p>}
      {helperText && !error && <p className="form-hint">{helperText}</p>}
    </div>
  )
}

export default Input
