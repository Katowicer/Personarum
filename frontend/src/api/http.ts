interface CsrfTokenResponse {
  token: string
  headerName: string
  parameterName: string
}

interface ProblemDetail {
  title?: string
  detail?: string
  status?: number
}

export interface DownloadedFile {
  blob: Blob
  fileName: string
}

export class ApiError extends Error {
  constructor(
    public readonly status: number,
    message: string,
  ) {
    super(message)
    this.name = 'ApiError'
  }
}

let csrfToken: string | null = null
let csrfHeaderName = 'X-CSRF-TOKEN'

function changesState(method: string): boolean {
  return !['GET', 'HEAD', 'OPTIONS'].includes(method)
}

export async function loadCsrfToken(): Promise<void> {
  const response = await fetch('/api/auth/csrf', {
    credentials: 'include',
  })

  if (!response.ok) {
    throw await createApiError(response)
  }

  const data = (await response.json()) as CsrfTokenResponse

  csrfToken = data.token
  csrfHeaderName = data.headerName
}

async function sendRequest(path: string, options: RequestInit = {}): Promise<Response> {
  const method = (options.method ?? 'GET').toUpperCase()

  if (changesState(method) && csrfToken === null) {
    await loadCsrfToken()
  }

  const headers = new Headers(options.headers)

  if (changesState(method) && csrfToken !== null) {
    headers.set(csrfHeaderName, csrfToken)
  }

  const response = await fetch(path, {
    ...options,
    headers,
    credentials: 'include',
  })

  if (!response.ok) {
    throw await createApiError(response)
  }

  return response
}

export async function apiRequest<T>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await sendRequest(path, options)

  if (response.status === 204) {
    return undefined as T
  }

  return (await response.json()) as T
}

export async function apiDownload(path: string): Promise<DownloadedFile> {
  const response = await sendRequest(path)

  return {
    blob: await response.blob(),
    fileName: readFileName(response),
  }
}

export function clearCsrfToken(): void {
  csrfToken = null
  csrfHeaderName = 'X-CSRF-TOKEN'
}

function readFileName(response: Response): string {
  const contentDisposition = response.headers.get('content-disposition')

  if (contentDisposition === null) {
    return 'documento'
  }

  const encodedMatch = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i)

  if (encodedMatch?.[1]) {
    try {
      return decodeURIComponent(encodedMatch[1])
    } catch {
      return encodedMatch[1]
    }
  }

  const plainMatch = contentDisposition.match(/filename="?([^";]+)"?/i)

  return plainMatch?.[1] ?? 'documento'
}

async function createApiError(response: Response): Promise<ApiError> {
  let message = `Errore HTTP ${response.status}`
  const contentType = response.headers.get('content-type') ?? ''

  if (
    contentType.includes('application/json') ||
    contentType.includes('application/problem+json')
  ) {
    try {
      const problem = (await response.json()) as ProblemDetail
      message = problem.detail ?? problem.title ?? message
    } catch {
      // TODO: per ora ignora
    }
  }

  return new ApiError(response.status, message)
}
