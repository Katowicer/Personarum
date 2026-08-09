import { apiRequest } from '@/api/http'

export interface DocumentTemplate {
  id: number
  name: string
  description: string | null
  content: string
  enabled: boolean
}

export interface CreateDocumentTemplatePayload {
  name: string
  description: string | null
  content: string
}

export interface UpdateDocumentTemplatePayload {
  name: string
  description: string | null
  content: string
  enabled: boolean
}

export function findEnabledTemplates(): Promise<DocumentTemplate[]> {
  return apiRequest<DocumentTemplate[]>('/api/templates')
}

export function findAllTemplates(): Promise<DocumentTemplate[]> {
  return apiRequest<DocumentTemplate[]>('/api/admin/templates')
}

export function findTemplateById(id: number): Promise<DocumentTemplate> {
  return apiRequest<DocumentTemplate>(`/api/admin/templates/${id}`)
}

export function createTemplate(payload: CreateDocumentTemplatePayload): Promise<DocumentTemplate> {
  return apiRequest<DocumentTemplate>('/api/admin/templates', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}

export function updateTemplate(
  id: number,
  payload: UpdateDocumentTemplatePayload,
): Promise<DocumentTemplate> {
  return apiRequest<DocumentTemplate>(`/api/admin/templates/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}
