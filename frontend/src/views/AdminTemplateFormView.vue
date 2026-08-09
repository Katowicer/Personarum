<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import {
  createTemplate,
  findTemplateById,
  updateTemplate,
} from '@/services/documentTemplateService'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const error = ref<string | null>(null)

const templateId = computed<number | null>(() => {
  const value = route.params.templateId

  if (typeof value !== 'string') {
    return null
  }

  const parsed = Number(value)

  return Number.isInteger(parsed) && parsed > 0 ? parsed : null
})

const editing = computed(() => templateId.value !== null)

const form = reactive({
  name: '',
  description: '',
  content: '',
  enabled: true,
})

async function loadTemplate(): Promise<void> {
  if (templateId.value === null) {
    return
  }

  loading.value = true
  error.value = null

  try {
    const template = await findTemplateById(templateId.value)

    form.name = template.name
    form.description = template.description ?? ''
    form.content = template.content
    form.enabled = template.enabled
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    loading.value = false
  }
}

async function save(): Promise<void> {
  error.value = null

  const name = form.name.trim()
  const content = form.content.trim()

  if (name === '') {
    error.value = 'Nome obbligatorio'
    return
  }

  if (content === '') {
    error.value = 'Contenuto obbligatorio'
    return
  }

  saving.value = true

  try {
    if (templateId.value === null) {
      await createTemplate({
        name,
        description: emptyToNull(form.description),
        content,
      })
    } else {
      await updateTemplate(templateId.value, {
        name,
        description: emptyToNull(form.description),
        content,
        enabled: form.enabled,
      })
    }

    await router.push({
      name: 'admin-templates',
    })
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    saving.value = false
  }
}

function emptyToNull(value: string): string | null {
  const normalized = value.trim()

  return normalized === '' ? null : normalized
}

function getErrorMessage(cause: unknown): string {
  if (cause instanceof Error) {
    return cause.message
  }

  return 'Si è verificato un errore'
}

onMounted(loadTemplate)
</script>

<template>
  <v-container>
    <v-btn
      variant="text"
      prepend-icon="mdi-arrow-left"
      :to="{ name: 'admin-templates' }"
      class="mb-4"
    >
      Template
    </v-btn>

    <h1 class="text-h4 mb-6">
      {{ editing ? 'Modifica template' : 'Nuovo template' }}
    </h1>

    <v-alert
      v-if="error"
      type="error"
      variant="tonal"
      closable
      class="mb-4"
      @click:close="error = null"
    >
      {{ error }}
    </v-alert>

    <v-progress-linear v-if="loading" indeterminate class="mb-4" />

    <v-card v-if="!loading">
      <v-card-text>
        <v-form @submit.prevent="save">
          <v-text-field
            v-model="form.name"
            label="Nome"
            maxlength="120"
            :disabled="saving"
            class="mb-2"
          />

          <v-textarea
            v-model="form.description"
            label="Descrizione"
            maxlength="500"
            rows="3"
            :disabled="saving"
            class="mb-2"
          />

          <v-textarea
            v-model="form.content"
            label="Contenuto template"
            rows="12"
            auto-grow
            :disabled="saving"
            class="mb-2"
          />

          <div>
            Todo: aggiungere sezione con l'elenco dei campi / placeholder utilizzabili: {firstName},
            {lastName}, {fiscalCode} ecc.
          </div>

          <v-switch
            v-if="editing"
            v-model="form.enabled"
            label="Template abilitato"
            color="primary"
            :disabled="saving"
          />

          <div class="d-flex ga-2 mt-4">
            <v-btn type="submit" color="primary" :loading="saving"> Salva </v-btn>

            <v-btn variant="outlined" :to="{ name: 'admin-templates' }" :disabled="saving">
              Annulla
            </v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>
