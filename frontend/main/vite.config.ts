import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/life-tracking': 'http://localhost:8081',
      '/auth': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
    },
  },
})
