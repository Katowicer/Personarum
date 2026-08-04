<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')

async function submitLogin(): Promise<void> {
  const authenticated = await authStore.login({
    username: username.value.trim(),
    password: password.value,
  })

  // Todo: spostare in guardia router
  if (authenticated) {
    await router.push('/profiles')
  }
}
</script>

<template>
  <v-container class="login-page d-flex align-center justify-center">
    <v-card width="420">
      <v-card-title class="text-h5"> Personarum </v-card-title>

      <v-card-subtitle> Accedi all'applicazione </v-card-subtitle>

      <v-card-text>
        <v-alert v-if="authStore.error" type="error" variant="tonal" class="mb-4">
          {{ authStore.error }}
        </v-alert>

        <form @submit.prevent="submitLogin">
          <v-text-field
            v-model="username"
            label="Username"
            autocomplete="username"
            :disabled="authStore.loading"
            required
          />

          <v-text-field
            v-model="password"
            label="Password"
            type="password"
            autocomplete="current-password"
            :disabled="authStore.loading"
            required
          />

          <v-btn
            type="submit"
            color="primary"
            block
            :loading="authStore.loading"
            :disabled="username.trim() === '' || password === ''"
          >
            Accedi
          </v-btn>
        </form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
}
</style>
