export const ROLES = {
  BEEKEEPER: 'BEEKEEPER',
  CUSTOMER: 'CUSTOMER',
  LAB: 'LAB',
  KVIC_OFFICER: 'KVIC_OFFICER',
  ADMIN: 'ADMIN',
}

export const ROLE_LABELS = {
  [ROLES.BEEKEEPER]: 'Beekeeper',
  [ROLES.CUSTOMER]: 'Customer',
  [ROLES.LAB]: 'Lab Technician',
  [ROLES.KVIC_OFFICER]: 'KVIC Officer',
  [ROLES.ADMIN]: 'Administrator',
}

export const ROLE_ROUTES = {
  [ROLES.BEEKEEPER]: '/beekeeper/dashboard',
  [ROLES.CUSTOMER]: '/customer/dashboard',
  [ROLES.LAB]: '/lab/dashboard',
  [ROLES.KVIC_OFFICER]: '/admin/dashboard',
  [ROLES.ADMIN]: '/admin/dashboard',
}
