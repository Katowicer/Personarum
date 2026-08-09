<script setup lang="ts">
import { onMounted, ref } from 'vue'

import {
  findAllTemplates,
  updateTemplate,
  type DocumentTemplate,
} from '@/services/documentTemplateService'

const templates = ref<DocumentTemplate[]>([])
const loading = ref(false)
const savingId = ref<number | null>(null)
const error = ref<string | null>(null)

async function loadTemplates(): Promise<void> {
  loading.value = true
  error.value = null

  try {
    templates.value = await findAllTemplates()
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    loading.value = false
  }
}

async function changeEnabled(template: DocumentTemplate, enabled: boolean): Promise<void> {
  savingId.value = template.id
  error.value = null

  try {
    const updated = await updateTemplate(template.id, {
      name: template.name,
      description: template.description,
      content: template.content,
      enabled,
    })

    Object.assign(template, updated)
  } catch (cause) {
    error.value = getErrorMessage(cause)
  } finally {
    savingId.value = null
  }
}

function getErrorMessage(cause: unknown): string {
  if (cause instanceof Error) {
    return cause.message
  }

  return 'Si è verificato un errore'
}

onMounted(loadTemplates)
</script>

<template>
  <v-container>
    <div class="d-flex align-center justify-space-between mb-6">
      <div>
        <v-btn variant="text" prepend-icon="mdi-arrow-left" :to="{ name: 'profiles' }" class="mb-2">
          Profili
        </v-btn>

        <h1 class="text-h4">Template documentali</h1>
      </div>

      <v-btn color="primary" prepend-icon="mdi-plus" :to="{ name: 'admin-template-create' }">
        Nuovo template
      </v-btn>
    </div>

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
      <v-card-text v-if="templates.length === 0"> Nessun template disponibile. </v-card-text>

      <v-table v-else>
        <thead>
          <tr>
            <th>Nome</th>
            <th>Descrizione</th>
            <th>Stato</th>
            <th class="text-right">Azioni</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="template in templates" :key="template.id">
            <td>
              {{ template.name }}
            </td>

            <td>
              {{ template.description ?? '-' }}
            </td>

            <td>
              <v-chip size="small" :color="template.enabled ? 'success' : undefined">
                {{ template.enabled ? 'Abilitato' : 'Disabilitato' }}
              </v-chip>
            </td>

            <td class="text-right">
              <v-btn
                variant="text"
                size="small"
                :to="{
                  name: 'admin-template-edit',
                  params: { templateId: template.id },
                }"
              >
                Modifica
              </v-btn>

              <v-btn
                v-if="template.enabled"
                variant="text"
                size="small"
                :loading="savingId === template.id"
                @click="changeEnabled(template, false)"
              >
                Disabilita
              </v-btn>

              <v-btn
                v-else
                variant="text"
                size="small"
                :loading="savingId === template.id"
                @click="changeEnabled(template, true)"
              >
                Abilita
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </v-card>
  </v-container>
</template>
