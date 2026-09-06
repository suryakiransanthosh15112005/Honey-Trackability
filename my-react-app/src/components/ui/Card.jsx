import React from 'react'

export const Card = ({ children, className = '', hover = true, padding = true, ...props }) => {
  return (
    <div
      className={`card${hover ? ' card--hover' : ''}${padding ? ' card--p' : ''} ${className}`}
      {...props}
    >
      {children}
    </div>
  )
}

export default Card
