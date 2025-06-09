import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
<<<<<<< HEAD
        additionalData: '@import "@/assets/style/mixin.scss";',
=======
        additionalData: '@use "@/assets/style/mixin.scss";',
>>>>>>> parent of 2c74f9e (Merge branch 'main' of https://github.com/ahiaccccc/QiHuang_TCM_system)
      },
    },
  },
})
