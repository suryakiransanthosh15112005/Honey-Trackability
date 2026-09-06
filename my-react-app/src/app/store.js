import { configureStore } from '@reduxjs/toolkit'
import authReducer from '../features/auth/authSlice'
import beekeeperReducer from '../features/beekeeper/beekeeperSlice'
import hiveReducer from '../features/hive/hiveSlice'
import batchReducer from '../features/batch/batchSlice'
import labReducer from '../features/lab/labSlice'
import cartReducer from '../features/cart/cartSlice'
import notificationReducer from '../features/notification/notificationSlice'

export const store = configureStore({
  reducer: {
    auth: authReducer,
    beekeeper: beekeeperReducer,
    hive: hiveReducer,
    batch: batchReducer,
    lab: labReducer,
    cart: cartReducer,
    notification: notificationReducer,
  },
  devTools: import.meta.env.DEV,
})

export default store
