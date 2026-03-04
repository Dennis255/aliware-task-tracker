import { globalIgnores } from 'eslint/config'
import { defineConfigWithVueTs, vueTsConfigs } from '@vue/eslint-config-typescript'
import pluginVue from 'eslint-plugin-vue'
import pluginOxlint from 'eslint-plugin-oxlint'
import skipFormatting from 'eslint-config-prettier/flat'

// To allow more languages other than `ts` in `.vue` files, uncomment the following lines:
// import { configureVueProject } from '@vue/eslint-config-typescript'
// configureVueProject({ scriptLangs: ['ts', 'tsx'] })
// More info at https://github.com/vuejs/eslint-config-typescript/#advanced-setup

export default defineConfigWithVueTs(
  {
    name: 'app/files-to-lint',
    files: ['**/*.{vue,ts,mts,tsx}'],
  },

  globalIgnores(['**/dist/**', '**/dist-ssr/**', '**/coverage/**']),

  ...pluginVue.configs['flat/essential'],
  vueTsConfigs.recommended,

  // --- AQUÍ ESTÁ LA SOLUCIÓN ---
  // Añadimos este objeto para desactivar manualmente las reglas estrictas
  {
    rules: {
      'vue/block-lang': 'off', // Permite <script> sin lang="ts"
      'vue/multi-word-component-names': 'off', // Permite nombres cortos como "Login"
      '@typescript-eslint/no-explicit-any': 'off', // Permite usar 'any' si fuera necesario
      '@typescript-eslint/no-unused-vars': 'off' // Evita errores por variables no usadas
    }
  },
  // -----------------------------

  ...pluginOxlint.buildFromOxlintConfigFile('.oxlintrc.json'),

  skipFormatting,
)