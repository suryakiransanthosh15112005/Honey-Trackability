import React from 'react'

export const LoadingSpinner = ({ size = 'md', text = 'Loading...' }) => {
  return (
    <div className="spinner-wrap">
      <div className={`spinner spinner--${size}`} />
      {text && <p className="spinner__text">{text}</p>}
    </div>
  )
}

export default LoadingSpinner
