import {defineConfig} from 'vite';
import vue from '@vitejs/plugin-vue';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: [
      { find: '@', replacement: new URL('./src', import.meta.url).pathname },
      { find: 'views', replacement: new URL('./src/views', import.meta.url).pathname },
      { find: 'components', replacement: new URL('./src/components', import.meta.url).pathname },
    ]
  }
});