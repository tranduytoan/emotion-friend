// API client for the backend
// In dev (vite proxy), calls go to http://localhost:8080
// In production, set VITE_API_BASE env var

const API_BASE = import.meta.env.VITE_API_BASE ?? ''

function getToken(): string {
  return localStorage.getItem('admin_token') ?? ''
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${getToken()}`,
      ...(options?.headers ?? {}),
    },
  })
  const json = await res.json()
  if (!json.success) throw new Error(json.error ?? 'Unknown error')
  return json.data as T
}

// ── Types ──────────────────────────────────────────────────────────────────

export interface ScenarioLesson {
  id: string
  title: string
  situation: string
  options: string[]
  correctIndex: number
  explanation: string
  sortOrder: number
}

export interface Story {
  id: string
  title: string
  content: string
  category: string
  imageUrl?: string | null
  sortOrder: number
}

export interface MusicTrack {
  id: string
  title: string
  artist: string
  filename: string
  sortOrder: number
}

// ── Scenario API ───────────────────────────────────────────────────────────

export const scenariosApi = {
  list: () => request<ScenarioLesson[]>('/admin/scenarios'),
  create: (data: Omit<ScenarioLesson, 'id'> & { id?: string }) =>
    request<ScenarioLesson>('/admin/scenarios', { method: 'POST', body: JSON.stringify(data) }),
  update: (id: string, data: Omit<ScenarioLesson, 'id'>) =>
    request<ScenarioLesson>(`/admin/scenarios/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id: string) => request<string>(`/admin/scenarios/${id}`, { method: 'DELETE' }),
}

// ── Story API ──────────────────────────────────────────────────────────────

export const storiesApi = {
  list: () => request<Story[]>('/admin/stories'),
  create: (data: Omit<Story, 'id'> & { id?: string }) =>
    request<Story>('/admin/stories', { method: 'POST', body: JSON.stringify(data) }),
  update: (id: string, data: Omit<Story, 'id'>) =>
    request<Story>(`/admin/stories/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id: string) => request<string>(`/admin/stories/${id}`, { method: 'DELETE' }),
}

// ── Music API ──────────────────────────────────────────────────────────────

export const musicApi = {
  list: () => request<MusicTrack[]>('/admin/music'),
  create: (data: Omit<MusicTrack, 'id'> & { id?: string }) =>
    request<MusicTrack>('/admin/music', { method: 'POST', body: JSON.stringify(data) }),
  update: (id: string, data: Omit<MusicTrack, 'id'>) =>
    request<MusicTrack>(`/admin/music/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id: string) => request<string>(`/admin/music/${id}`, { method: 'DELETE' }),
}

// ── Auth helper ────────────────────────────────────────────────────────────

/** Verify the token by hitting an admin endpoint. Returns true if valid. */
export async function verifyToken(token: string): Promise<boolean> {
  try {
    const res = await fetch(`${API_BASE}/admin/scenarios`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const json = await res.json()
    return json.success === true
  } catch {
    return false
  }
}
