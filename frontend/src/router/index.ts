import { createRouter, createWebHistory } from 'vue-router'

import { useAuthStore } from '@/stores/auth'
import LoginView from '@/views/LoginView.vue'
import ProfileDetailView from '@/views/ProfileDetailView.vue'
import ProfileDocumentFormView from '@/views/ProfileDocumentFormView.vue'
import ProfileFormView from '@/views/ProfileFormView.vue'
import ProfilesView from '@/views/ProfilesView.vue'
import AdminUserFormView from '@/views/AdminUserFormView.vue'
import AdminUsersView from '@/views/AdminUsersView.vue'
import AdminTemplatesView from '@/views/AdminTemplatesView.vue'
import AdminTemplateFormView from '@/views/AdminTemplateFormView.vue'
import GeneratedDocumentsView from '@/views/GeneratedDocumentsView.vue'

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
      path: '/admin/users',
      name: 'admin-users',
      component: AdminUsersView,
      meta: {
        requiresAuth: true,
        requiresAdmin: true,
      },
    },
    {
      path: '/admin/users/new',
      name: 'admin-user-create',
      component: AdminUserFormView,
      meta: {
        requiresAuth: true,
        requiresAdmin: true,
      },
    },
    {
      path: '/admin/users/:userId/edit',
      name: 'admin-user-edit',
      component: AdminUserFormView,
      meta: {
        requiresAuth: true,
        requiresAdmin: true,
      },
    },
    {
      path: '/admin/templates',
      name: 'admin-templates',
      component: AdminTemplatesView,
      meta: {
        requiresAuth: true,
        requiresAdmin: true,
      },
    },
    {
      path: '/admin/templates/new',
      name: 'admin-template-create',
      component: AdminTemplateFormView,
      meta: {
        requiresAuth: true,
        requiresAdmin: true,
      },
    },
    {
      path: '/admin/templates/:templateId/edit',
      name: 'admin-template-edit',
      component: AdminTemplateFormView,
      meta: {
        requiresAuth: true,
        requiresAdmin: true,
      },
    },
    {
      path: '/profiles',
      name: 'profiles',
      component: ProfilesView,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: '/profiles/new',
      name: 'profile-create',
      component: ProfileFormView,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: '/profiles/:profileId/documents/new',
      name: 'profile-document-create',
      component: ProfileDocumentFormView,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: '/profiles/:profileId/documents/:documentId/edit',
      name: 'profile-document-edit',
      component: ProfileDocumentFormView,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: '/profiles/:profileId/edit',
      name: 'profile-edit',
      component: ProfileFormView,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: '/profiles/:profileId',
      name: 'profile-detail',
      component: ProfileDetailView,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: '/profiles/:profileId/generated-documents',
      name: 'profile-generated-documents',
      component: GeneratedDocumentsView,
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

  if (to.meta.requiresAdmin && authStore.user?.role !== 'ADMIN') {
    return {
      name: 'profiles',
    }
  }

  if (to.name === 'login' && authStore.isAuthenticated) {
    return {
      name: 'profiles',
    }
  }
})

export default router
