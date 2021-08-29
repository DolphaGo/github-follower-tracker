import { CONFIG } from '@/config'
import axios from 'axios'

export const request = axios.create({
  baseURL: `${CONFIG.VITE_ADMIN_API_URL || ''}`,
  headers: {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET,PUT,POST,DELETE',
  },
  params: {},
})
