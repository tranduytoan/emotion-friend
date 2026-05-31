import { useState, useEffect, useCallback } from 'react'
import { LessonTopic, scenariosApi, ScenarioLesson, topicsApi } from '../api'

const EMOTION_TYPES = ['HAPPY', 'SAD', 'ANGRY', 'SURPRISED', 'CALM', 'TIRED']
const EMOTION_LABELS: Record<string, string> = {
  HAPPY: '😊 Vui', SAD: '😢 Buồn', ANGRY: '😠 Tức giận',
  SURPRISED: '😮 Ngạc nhiên', CALM: '😌 Bình tĩnh', TIRED: '😴 Mệt mỏi',
}

type FormData = Omit<ScenarioLesson, 'id'>

const EMPTY_FORM: FormData = {
  title: '', situation: '', imageName: '', options: ['HAPPY', 'SAD', 'ANGRY', 'SURPRISED'], correctEmotion: 'HAPPY', explanation: '', sortOrder: 0, topicId: null,
}

const STATIC_BASE = (import.meta.env.VITE_API_BASE ?? '').replace(/\/$/, '')

function scenarioImageUrl(imageName?: string | null): string {
  if (!imageName) return ''
  return `${STATIC_BASE}/img/scenario_lessons/${imageName}`
}

function sortByNewestId(items: ScenarioLesson[]): ScenarioLesson[] {
  return [...items].sort((a, b) => b.id - a.id)
}

export default function ScenariosPage() {
  const [items, setItems] = useState<ScenarioLesson[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [modal, setModal] = useState<{ open: boolean; editing?: ScenarioLesson }>({ open: false })
  const [form, setForm] = useState<FormData>(EMPTY_FORM)
  const [selectedImageFile, setSelectedImageFile] = useState<File | null>(null)
  const [selectedImagePreviewUrl, setSelectedImagePreviewUrl] = useState('')
  const [saving, setSaving] = useState(false)
  const [topics, setTopics] = useState<LessonTopic[]>([])
  const [toast, setToast] = useState<{ msg: string; type: 'success' | 'error' } | null>(null)

  const showToast = (msg: string, type: 'success' | 'error' = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3000)
  }

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const [scenarioItems, topicItems] = await Promise.all([scenariosApi.list(), topicsApi.list()])
      setItems(sortByNewestId(scenarioItems))
      setTopics(topicItems)
      setError('')
    } catch (e) {
      setError(String(e))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { load() }, [load])

  useEffect(() => {
    if (!selectedImageFile) {
      setSelectedImagePreviewUrl('')
      return
    }
    const objectUrl = URL.createObjectURL(selectedImageFile)
    setSelectedImagePreviewUrl(objectUrl)
    return () => URL.revokeObjectURL(objectUrl)
  }, [selectedImageFile])

  function openCreate() {
    setForm(EMPTY_FORM)
    setSelectedImageFile(null)
    setModal({ open: true })
  }

  function openEdit(item: ScenarioLesson) {
    setForm({
      title: item.title,
      situation: item.situation,
      imageName: item.imageName ?? '',
      options: item.options,
      correctEmotion: item.correctEmotion,
      explanation: item.explanation,
      sortOrder: item.sortOrder,
      topicId: item.topicId ?? null,
    })
    setSelectedImageFile(null)
    setModal({ open: true, editing: item })
  }

  async function handleSave(e: React.FormEvent) {
    e.preventDefault()
    setSaving(true)
    try {
      let saved: ScenarioLesson
      if (modal.editing) {
        saved = await scenariosApi.update(modal.editing.id, form)
        showToast('Đã cập nhật bài học!')
      } else {
        saved = await scenariosApi.create(form)
        showToast('Đã thêm bài học mới!')
      }

      if (selectedImageFile) {
        await scenariosApi.uploadImage(saved.id, selectedImageFile)
        showToast('Đã tải ảnh tình huống thành công!')
      }

      setSelectedImageFile(null)
      setModal({ open: false })
      load()
    } catch (e) {
      showToast(String(e), 'error')
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(item: ScenarioLesson) {
    if (!confirm(`Xóa bài học "${item.title}"?`)) return
    try {
      await scenariosApi.delete(item.id)
      showToast('Đã xóa!')
      load()
    } catch (e) {
      showToast(String(e), 'error')
    }
  }

  function setOption(idx: number, val: string) {
    const opts = [...form.options]
    opts[idx] = val
    setForm(f => ({ ...f, options: opts }))
  }

  return (
    <>
      <div className="card">
        <div className="card-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <span className="card-title">Danh sách bài học</span>
            <span className="card-count">{items.length} bài</span>
          </div>
          <button className="btn btn-primary" onClick={openCreate}>+ Thêm bài học</button>
        </div>
        <div className="table-wrap">
          {loading ? (
            <div className="empty-state"><div className="spinner" style={{ width: 30, height: 30, margin: '0 auto' }} /></div>
          ) : error ? (
            <div className="empty-state"><div className="empty-icon">⚠️</div><div className="empty-text">{error}</div></div>
          ) : items.length === 0 ? (
            <div className="empty-state"><div className="empty-icon">📚</div><div className="empty-text">Chưa có bài học nào</div></div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>#</th>
                  <th>ID</th>
                  <th>Tiêu đề</th>
                  <th>Tình huống</th>
                  <th>Ảnh</th>
                  <th>Số lựa chọn</th>
                  <th>Đáp án đúng</th>
                  <th>Chủ đề</th>
                  <th>Thứ tự</th>
                  <th>Hành động</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item, i) => (
                  <tr key={item.id}>
                    <td style={{ color: '#94a3b8' }}>{i + 1}</td>
                    <td><span className="badge badge-blue">{item.id}</span></td>
                    <td><strong>{item.title}</strong></td>
                    <td className="td-truncate">{item.situation}</td>
                    <td>
                      {item.imageName ? (
                        <div className="scenario-image-cell">
                          <img src={scenarioImageUrl(item.imageName)} alt={item.title} />
                          <span className="badge badge-purple">{item.imageName}</span>
                        </div>
                      ) : (
                        <span className="badge">Chưa có ảnh</span>
                      )}
                    </td>
                    <td><span className="badge badge-blue">{item.options.length} lựa chọn</span></td>
                    <td><span className="badge badge-green">{EMOTION_LABELS[item.correctEmotion] ?? item.correctEmotion}</span></td>
                    <td>{topics.find(t => t.id === item.topicId)?.title ?? '-'}</td>
                    <td>{item.sortOrder}</td>
                    <td>
                      <div className="td-actions">
                        <button className="btn btn-edit btn-sm" onClick={() => openEdit(item)}>✏️ Sửa</button>
                        <button className="btn btn-danger btn-sm" onClick={() => handleDelete(item)}>🗑️</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {/* Modal */}
      {modal.open && (
        <div className="modal-backdrop" onClick={() => setModal({ open: false })}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <span className="modal-title">{modal.editing ? 'Sửa bài học' : 'Thêm bài học mới'}</span>
              <button className="modal-close" onClick={() => setModal({ open: false })}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Tiêu đề *</label>
                  <input className="form-input" value={form.title} onChange={e => setForm(f => ({ ...f, title: e.target.value }))} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Tình huống *</label>
                  <textarea className="form-textarea" value={form.situation} onChange={e => setForm(f => ({ ...f, situation: e.target.value }))} required rows={3} />
                </div>
                <div className="form-group">
                  <label className="form-label">Ảnh tình huống</label>
                  {selectedImagePreviewUrl && (
                    <div className="scenario-image-preview scenario-image-preview-small">
                      <img src={selectedImagePreviewUrl} alt="Ảnh vừa chọn" />
                      <div className="form-hint">Xem trước ảnh mới: {selectedImageFile?.name}</div>
                    </div>
                  )}
                  {form.imageName ? (
                    <div className="scenario-image-preview">
                      <img src={scenarioImageUrl(form.imageName)} alt="Scenario" />
                      <div className="form-hint">Ảnh hiện tại: {form.imageName}</div>
                    </div>
                  ) : (
                    <div className="form-hint">Chưa có ảnh cho bài học này.</div>
                  )}
                  <input
                    className="form-input"
                    type="file"
                    accept="image/*"
                    onChange={e => setSelectedImageFile(e.target.files?.[0] ?? null)}
                  />
                  <div className="form-hint">
                    Ảnh sẽ lưu tự động vào thư mục res/img/scenario_lessons với tên <strong>{modal.editing ? `${modal.editing.id}.png` : '[id].png'}</strong>.
                  </div>
                </div>
                <div className="form-group">
                  <label className="form-label">Chủ đề</label>
                  <select
                    className="form-input"
                    value={form.topicId ?? ''}
                    onChange={e => setForm(f => ({ ...f, topicId: e.target.value ? Number(e.target.value) : null }))}
                  >
                    <option value="">-- Chưa gán chủ đề --</option>
                    {topics.map(topic => (
                      <option key={topic.id} value={topic.id}>
                        {topic.title} (Mức {topic.difficulty})
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Các lựa chọn * (chọn EmotionType cho mỗi ô)</label>
                  <div className="options-list">
                    {form.options.map((opt, i) => (
                      <div className="option-row" key={i}>
                        <div
                          className={`option-index${form.correctEmotion === opt ? ' correct' : ''}`}
                          title="Nhấn để chọn đây là đáp án đúng"
                          style={{ cursor: 'pointer' }}
                          onClick={() => setForm(f => ({ ...f, correctEmotion: opt }))}
                        >
                          {form.correctEmotion === opt ? '✓' : i + 1}
                        </div>
                        <select
                          className="form-input"
                          value={opt}
                          onChange={e => setOption(i, e.target.value)}
                          required
                        >
                          {EMOTION_TYPES.map(et => (
                            <option key={et} value={et}>{EMOTION_LABELS[et]}</option>
                          ))}
                        </select>
                      </div>
                    ))}
                  </div>
                  <div className="form-hint">Nhấn vào số thứ tự để đánh dấu đáp án đúng (hiển thị màu xanh)</div>
                </div>
                <div className="form-group">
                  <label className="form-label">Đáp án đúng *</label>
                  <select className="form-input" value={form.correctEmotion} onChange={e => setForm(f => ({ ...f, correctEmotion: e.target.value }))} required>
                    {EMOTION_TYPES.map(et => (
                      <option key={et} value={et}>{EMOTION_LABELS[et]}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Giải thích *</label>
                  <textarea className="form-textarea" value={form.explanation} onChange={e => setForm(f => ({ ...f, explanation: e.target.value }))} required rows={2} />
                </div>
                <div className="form-group">
                  <label className="form-label">Thứ tự sắp xếp</label>
                  <input className="form-input" type="number" value={form.sortOrder} onChange={e => setForm(f => ({ ...f, sortOrder: Number(e.target.value) }))} />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setModal({ open: false })}>Hủy</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? <><span className="spinner" /> Đang lưu...</> : (modal.editing ? '💾 Cập nhật' : '➕ Thêm mới')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && (
        <div className="toast-container">
          <div className={`toast ${toast.type}`}>{toast.msg}</div>
        </div>
      )}
    </>
  )
}
