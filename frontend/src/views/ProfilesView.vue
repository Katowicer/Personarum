<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<template>
  <v-container>
    <h1 class="text-h4 mb-4">Profili</h1>
    <div v-if="authStore.user">
      <p>Utente: {{ authStore.user.username }}</p>
      <p>Ruolo: {{ authStore.user.role }}</p>
    </div>

    <p>TODO: vista dei profili e logiche</p>
  </v-container>
</template>
