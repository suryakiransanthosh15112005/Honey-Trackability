import React from 'react'

/**
 * DataTable — Standardized Data Table wrapper component.
 * Prevents horizontal page overflow by using an overflow-x container.
 */
export const DataTable = ({ children, className = '' }) => {
  return (
    <div className={`w-full overflow-x-auto rounded-xl border border-slate-200 shadow-sm bg-white ${className}`}>
      <table className="w-full text-left border-collapse text-sm">
        {children}
      </table>
    </div>
  )
}

export default DataTable
