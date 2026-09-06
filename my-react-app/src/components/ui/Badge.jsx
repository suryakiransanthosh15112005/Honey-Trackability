import React from 'react'

export const Badge = ({ children, variant = 'primary', className = '', ...props }) => {
  return (
    <span className={`badge badge--${variant} ${className}`} {...props}>
      {children}
    </span>
  )
}

export default Badge
