// API client for the backend.
// In dev (Vite proxy), calls go through the local dev server.
// In production, set VITE_API_BASE if the UI is not served from the same origin as the backend.

const API_BASE = import.meta.env.VITE_API_BASE ?? ''
const REQUEST_TIMEOUT_MS = 8000

function getToken(): string {
  return localStorage.getItem('admin_token') ?? ''
}

async function fetchJson<T>(path: string, options?: RequestInit): Promise<T> {
  const controller = new AbortController()
  const timeoutId = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS)

  try {
    const res = await fetch(`${API_BASE}${path}`, {
      ...options,
      cache: 'no-store',
      signal: controller.signal,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${getToken()}`,
        ...(options?.headers),
      },
    })

    const text = await res.text()
    const json = text ? JSON.parse(text) : null
    if (!res.ok) {
      throw new Error(json?.error ?? `Request failed with status ${res.status}`)
    }
    if (!json?.success) throw new Error(json?.error ?? 'Unknown error')
    return json.data as T
  } finally {
    window.clearTimeout(timeoutId)
  }
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  return fetchJson<T>(path, options)
}

// ── Types ──────────────────────────────────────────────────────────────────

export interface ScenarioLesson {
  id: number
  title: string
  situation: string
  options: string[]         // EmotionType codes: HAPPY, SAD, ANGRY, SURPRISED, CALM, TIRED
  correctEmotion: string   // EmotionType code of correct answer
  explanation: string
  sortOrder: number
  topicId?: number | null
}

export interface LessonTopic {
  id: number
  title: string
  description: string
  difficulty: number
  sortOrder: number
}

export interface Story {
  id: number
  title: string
  content: string
  category: string
  imageUrl?: string | null
  sortOrder: number
  imageFolder?: string | null
}

export interface MusicTrack {
  id: number
  title: string
  artist: string
  filename: string
  sortOrder: number
}

// ── Scenario API ───────────────────────────────────────────────────────────

export const scenariosApi = {
  list: () => request<ScenarioLesson[]>('/admin/scenarios'),
  create: (data: Omit<ScenarioLesson, 'id'>) =>
    request<ScenarioLesson>('/admin/scenarios', { method: 'POST', body: JSON.stringify(data) }),
  update: (id: number, data: Omit<ScenarioLesson, 'id'>) =>
    request<ScenarioLesson>(`/admin/scenarios/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id: number) => request<string>(`/admin/scenarios/${id}`, { method: 'DELETE' }),
}

// ── Topic API ──────────────────────────────────────────────────────────────

export const topicsApi = {
  list: () => request<LessonTopic[]>('/admin/topics'),
  create: (data: Omit<LessonTopic, 'id'>) =>
    request<LessonTopic>('/admin/topics', { method: 'POST', body: JSON.stringify(data) }),
  update: (id: number, data: Omit<LessonTopic, 'id'>) =>
    request<LessonTopic>(`/admin/topics/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id: number) => request<string>(`/admin/topics/${id}`, { method: 'DELETE' }),
}

// ── Story API ──────────────────────────────────────────────────────────────

export const storiesApi = {
  list: () => request<Story[]>('/admin/stories'),
  create: (data: Omit<Story, 'id'>) =>
    request<Story>('/admin/stories', { method: 'POST', body: JSON.stringify(data) }),
  update: (id: number, data: Omit<Story, 'id'>) =>
    request<Story>(`/admin/stories/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id: number) => request<string>(`/admin/stories/${id}`, { method: 'DELETE' }),
}

// ── Music API ──────────────────────────────────────────────────────────────

export const musicApi = {
  list: () => request<MusicTrack[]>('/admin/music'),
  create: (data: Omit<MusicTrack, 'id'>) =>
    request<MusicTrack>('/admin/music', { method: 'POST', body: JSON.stringify(data) }),
  update: (id: number, data: Omit<MusicTrack, 'id'>) =>
    request<MusicTrack>(`/admin/music/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id: number) => request<string>(`/admin/music/${id}`, { method: 'DELETE' }),
}

// ── Auth helper ────────────────────────────────────────────────────────────

/** Verify the token by hitting an admin endpoint. Returns true if valid. */
export async function verifyToken(token: string): Promise<boolean> {
  try {
    const controller = new AbortController()
    const timeoutId = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS)
    try {
      const res = await fetch(`${API_BASE}/admin/scenarios`, {
        cache: 'no-store',
        signal: controller.signal,
        headers: { Authorization: `Bearer ${token}` },
      })
      const text = await res.text()
      const json = text ? JSON.parse(text) : null
      return res.ok && json?.success === true
    } finally {
      window.clearTimeout(timeoutId)
    }
  } catch {
    return false
  }
}
