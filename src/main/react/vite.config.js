import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
    root: path.resolve(__dirname, 'src'),
    publicDir: path.resolve(__dirname, 'public'),
    resolve: {
        alias: {
        '~bootstrap': path.resolve(__dirname, 'node_modules/bootstrap'),
        }
    },
    plugins: [react()],
    server: {
        hot: true,
        host: '0.0.0.0',
        watch: {
            usePolling: true,
        },
    proxy: {
        '/api': {
            target: 'http://localhost:8080',
            changeOrigin: true,
            secure: false,
            rewrite: (path) => path.replace(/^\/api/, ''),
        },
    },
    },
})
