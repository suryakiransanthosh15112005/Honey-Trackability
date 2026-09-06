import { ROLES } from '../constants/roles'

/**
 * Centralized Role Navigation Configuration
 * Defines sidebar groups, navigation items, routes, icons, groupKey and itemKey for i18n.
 */
export const ROLE_NAVIGATION = {
  [ROLES.BEEKEEPER]: [
    {
      group: 'Overview',
      groupKey: 'overview',
      items: [
        { label: 'Dashboard', itemKey: 'dashboard', route: '/beekeeper/dashboard', icon: '📊' },
      ],
    },
    {
      group: 'Hive Management',
      groupKey: 'hive_management',
      items: [
        { label: 'My Hives', itemKey: 'my_hives', route: '/hives', icon: '🐝' },
        { label: 'Hive Health & IoT', itemKey: 'hive_health_and_iot', route: '/hives/health', icon: '📡' },
      ],
    },
    {
      group: 'Production',
      groupKey: 'production',
      items: [
        { label: 'My Batches', itemKey: 'my_batches', route: '/batches', icon: '🍯' },
        { label: 'New Batch', itemKey: 'new_batch', route: '/batches/new', icon: '➕' },
      ],
    },
    {
      group: 'Marketplace & Orders',
      groupKey: 'marketplace_and_orders',
      items: [
        { label: 'My Products', itemKey: 'my_products', route: '/my-products', icon: '🏷️' },
        { label: 'Fulfillment Orders', itemKey: 'fulfillment_orders', route: '/beekeeper/orders', icon: '📦' },
        { label: 'Earnings & Revenue', itemKey: 'earnings_and_revenue', route: '/beekeeper/earnings', icon: '💰' },
      ],
    },
    {
      group: 'Account',
      groupKey: 'account',
      items: [
        { label: 'Notifications', itemKey: 'notifications', route: '/notifications', icon: '🔔' },
        { label: 'Profile & KVIC ID', itemKey: 'profile_and_kvic_id', route: '/beekeeper/profile', icon: '👤' },
      ],
    },
  ],

  [ROLES.CUSTOMER]: [
    {
      group: 'Overview',
      groupKey: 'overview',
      items: [
        { label: 'Customer Dashboard', itemKey: 'customer_dashboard', route: '/customer/dashboard', icon: '🛒' },
      ],
    },
    {
      group: 'Shopping',
      groupKey: 'shopping',
      items: [
        { label: 'Honey Marketplace', itemKey: 'honey_marketplace', route: '/marketplace', icon: '🛍️' },
        { label: 'Cart', itemKey: 'cart', route: '/cart', icon: '🛍️' },
      ],
    },
    {
      group: 'Orders & Disputes',
      groupKey: 'orders_and_disputes',
      items: [
        { label: 'My Orders', itemKey: 'my_orders', route: '/orders', icon: '📦' },
        { label: 'My Product Reviews', itemKey: 'my_product_reviews', route: '/my-reviews', icon: '⭐' },
        { label: 'Disputes & Support', itemKey: 'disputes_and_support', route: '/customer/disputes', icon: '🛡️' },
      ],
    },
    {
      group: 'Account',
      groupKey: 'account',
      items: [
        { label: 'Notifications', itemKey: 'notifications', route: '/notifications', icon: '🔔' },
        { label: 'Customer Profile', itemKey: 'customer_profile', route: '/customer/profile', icon: '👤' },
      ],
    },
  ],

  [ROLES.LAB]: [
    {
      group: 'Overview',
      groupKey: 'overview',
      items: [
        { label: 'Lab Dashboard', itemKey: 'lab_dashboard', route: '/lab/dashboard', icon: '🔬' },
      ],
    },
    {
      group: 'Testing Queue',
      groupKey: 'testing_queue',
      items: [
        { label: 'Pending Batch Tests', itemKey: 'pending_batch_tests', route: '/lab/tests/pending', icon: '🧪' },
        { label: 'Test History & Certs', itemKey: 'test_history_and_certs', route: '/lab/history', icon: '📜' },
      ],
    },
    {
      group: 'Account',
      groupKey: 'account',
      items: [
        { label: 'Notifications', itemKey: 'notifications', route: '/notifications', icon: '🔔' },
      ],
    },
  ],

  [ROLES.ADMIN]: [
    {
      group: 'Overview',
      groupKey: 'overview',
      items: [
        { label: 'Admin Dashboard', itemKey: 'admin_dashboard', route: '/admin/dashboard', icon: '⚡' },
        { label: 'System Analytics', itemKey: 'system_analytics', route: '/admin/analytics', icon: '📈' },
      ],
    },
    {
      group: 'Management',
      groupKey: 'management',
      items: [
        { label: 'Beekeepers', itemKey: 'beekeepers', route: '/admin/beekeepers', icon: '👨‍🌾' },
        { label: 'Batches & Quality', itemKey: 'batches_and_quality', route: '/admin/batches', icon: '🍯' },
        { label: 'Hives', itemKey: 'hives', route: '/admin/hives', icon: '🐝' },
        { label: 'Lab Audits', itemKey: 'lab_audits', route: '/admin/lab', icon: '🔬' },
        { label: 'Customer Orders', itemKey: 'customer_orders', route: '/admin/orders', icon: '📦' },
      ],
    },
    {
      group: 'Governance & Risk',
      groupKey: 'governance_and_risk',
      items: [
        { label: 'Verification Risk', itemKey: 'verification_risk', route: '/admin/verification-risk', icon: '🛡️' },
        { label: 'Disputes Overview', itemKey: 'disputes_overview', route: '/admin/disputes', icon: '⚖️' },
      ],
    },
    {
      group: 'Account',
      groupKey: 'account',
      items: [
        { label: 'Notifications', itemKey: 'notifications', route: '/notifications', icon: '🔔' },
      ],
    },
  ],

  [ROLES.KVIC_OFFICER]: [
    {
      group: 'Overview',
      groupKey: 'overview',
      items: [
        { label: 'Officer Dashboard', itemKey: 'officer_dashboard', route: '/admin/dashboard', icon: '🏛️' },
        { label: 'Regional Analytics', itemKey: 'regional_analytics', route: '/admin/analytics', icon: '📊' },
      ],
    },
    {
      group: 'Monitoring & Audit',
      groupKey: 'monitoring_and_audit',
      items: [
        { label: 'Registered Beekeepers', itemKey: 'registered_beekeepers', route: '/admin/beekeepers', icon: '👨‍🌾' },
        { label: 'Customer Orders', itemKey: 'customer_orders', route: '/admin/orders', icon: '📦' },
        { label: 'Batch Records', itemKey: 'batch_records', route: '/admin/batches', icon: '🍯' },
        { label: 'Hive Locations', itemKey: 'hive_locations', route: '/admin/hives', icon: '📍' },
        { label: 'Lab Compliance', itemKey: 'lab_compliance', route: '/admin/lab', icon: '🧪' },
        { label: 'Dispute Resolutions', itemKey: 'dispute_resolutions', route: '/admin/disputes', icon: '⚖️' },
        { label: 'Verification Risk Audit', itemKey: 'verification_risk_audit', route: '/admin/verification-risk', icon: '🛡️' },
      ],
    },
    {
      group: 'Account',
      groupKey: 'account',
      items: [
        { label: 'Notifications', itemKey: 'notifications', route: '/notifications', icon: '🔔' },
      ],
    },
  ],
}

export default ROLE_NAVIGATION
