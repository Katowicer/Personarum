import { createRouter, createWebHistory } from 'vue-router'

import LoginView from '@/views/LoginView.vue'
import ProfilesView from '@/views/ProfilesView.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),

  routes: [
    {
      path: '/',
      redirect: '/profiles',
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/profiles',
      name: 'profiles',
      component: ProfilesView,
      meta: {
        requiresAuth: true,
      },
    },
  ],
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()

  await authStore.restoreSession()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return {
      name: 'login',
    }
  }

  if (to.name === 'login' && authStore.isAuthenticated) {
    return {
      name: 'profiles',
    }
  }
})

export default router
