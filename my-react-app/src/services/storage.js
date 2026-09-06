const TOKEN_KEY = 'honeychain_token'
const USER_KEY = 'honeychain_user'
const ROLE_KEY = 'honeychain_role'

export const getStoredToken = () => localStorage.getItem(TOKEN_KEY)
export const setStoredToken = (token) => localStorage.setItem(TOKEN_KEY, token)
export const removeStoredToken = () => {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
  localStorage.removeItem(ROLE_KEY)
}

export const getStoredUser = () => {
  const data = localStorage.getItem(USER_KEY)
  try {
    return data ? JSON.parse(data) : null
  } catch {
    return null
  }
}
export const setStoredUser = (user) => localStorage.setItem(USER_KEY, JSON.stringify(user))

export const getStoredRole = () => localStorage.getItem(ROLE_KEY)
export const setStoredRole = (role) => localStorage.setItem(ROLE_KEY, role)
