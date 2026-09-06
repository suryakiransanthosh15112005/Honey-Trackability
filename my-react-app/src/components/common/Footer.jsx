import React from 'react'

export const Footer = () => {
  return (
    <footer id="main-footer" className="footer">
      <div className="footer__brand">
        <span>🍯</span>
        <span className="text-gradient">HoneyChain</span>
      </div>
      <p className="footer__copy">
        Blockchain-powered honey traceability © {new Date().getFullYear()}
      </p>
    </footer>
  )
}

export default Footer
