import { useState, useEffect, useCallback } from 'react'
import { storiesApi, Story } from '../api'

type FormData = Omit<Story, 'id'> & { id?: string }

const EMPTY_FORM: FormData = {
  title: '', content: '', category: 'general', imageUrl: '', sortOrder: 0,
}

const CATEGORIES = [
  { value: 'general', label: 'Tổng hợp' },
  { value: 'anger', label: 'Cơn tức giận' },
  { value: 'sadness', label: 'Nỗi buồn' },
  { value: 'anxiety', label: 'Lo lắng' },
  { value: 'happiness', label: 'Niềm vui' },
  { value: 'fear', label: 'Sợ hãi' },
]

export default function StoriesPage() {
  const [items, setItems] = useState<Story[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [modal, setModal] = useState<{ open: boolean; editing?: Story }>({ open: false })
  const [form, setForm] = useState<FormData>(EMPTY_FORM)
  const [saving, setSaving] = useState(false)
  const [toast, setToast] = useState<{ msg: string; type: 'success' | 'error' } | null>(null)

  const showToast = (msg: string, type: 'success' | 'error' = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3000)
  }

  const load = useCallback(async () => {
    setLoading(true)
    try {
      setItems(await storiesApi.list())
      setError('')
    } catch (e) {
      setError(String(e))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { load() }, [load])

  function openCreate() {
    setForm(EMPTY_FORM)
    setModal({ open: true })
  }

  function openEdit(item: Story) {
    setForm({ ...item, imageUrl: item.imageUrl ?? '' })
    setModal({ open: true, editing: item })
  }

  async function handleSave(e: React.FormEvent) {
    e.preventDefault()
    setSaving(true)
    const payload = { ...form, imageUrl: form.imageUrl?.trim() || undefined }
    try {
      if (modal.editing) {
        await storiesApi.update(modal.editing.id, payload as Omit<Story, 'id'>)
        showToast('Đã cập nhật câu chuyện!')
      } else {
        await storiesApi.create(payload)
        showToast('Đã thêm câu chuyện mới!')
      }
      setModal({ open: false })
      load()
    } catch (e) {
      showToast(String(e), 'error')
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(item: Story) {
    if (!confirm(`Xóa câu chuyện "${item.title}"?`)) return
    try {
      await storiesApi.delete(item.id)
      showToast('Đã xóa!')
      load()
    } catch (e) {
      showToast(String(e), 'error')
    }
  }

  const categoryLabel = (cat: string) => CATEGORIES.find(c => c.value === cat)?.label ?? cat

  return (
    <>
      <div className="card">
        <div className="card-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <span className="card-title">Danh sách câu chuyện</span>
            <span className="card-count">{items.length} truyện</span>
          </div>
          <button className="btn btn-primary" onClick={openCreate}>+ Thêm câu chuyện</button>
        </div>
        <div className="table-wrap">
          {loading ? (
            <div className="empty-state"><div className="spinner" style={{ width: 30, height: 30, margin: '0 auto' }} /></div>
          ) : error ? (
            <div className="empty-state"><div className="empty-icon">⚠️</div><div className="empty-text">{error}</div></div>
          ) : items.length === 0 ? (
            <div className="empty-state"><div className="empty-icon">📖</div><div className="empty-text">Chưa có câu chuyện nào</div></div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>#</th>
                  <th>Tiêu đề</th>
                  <th>Nội dung</th>
                  <th>Chủ đề</th>
                  <th>Thứ tự</th>
                  <th>Hành động</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item, i) => (
                  <tr key={item.id}>
                    <td style={{ color: '#94a3b8' }}>{i + 1}</td>
                    <td><strong>{item.title}</strong></td>
                    <td className="td-truncate">{item.content}</td>
                    <td><span className="badge badge-purple">{categoryLabel(item.category)}</span></td>
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

      {modal.open && (
        <div className="modal-backdrop" onClick={() => setModal({ open: false })}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <span className="modal-title">{modal.editing ? 'Sửa câu chuyện' : 'Thêm câu chuyện mới'}</span>
              <button className="modal-close" onClick={() => setModal({ open: false })}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Tiêu đề *</label>
                  <input className="form-input" value={form.title} onChange={e => setForm(f => ({ ...f, title: e.target.value }))} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Nội dung *</label>
                  <textarea className="form-textarea" value={form.content} onChange={e => setForm(f => ({ ...f, content: e.target.value }))} required rows={5} />
                </div>
                <div className="form-group">
                  <label className="form-label">Chủ đề cảm xúc</label>
                  <select className="form-select" value={form.category} onChange={e => setForm(f => ({ ...f, category: e.target.value }))}>
                    {CATEGORIES.map(c => <option key={c.value} value={c.value}>{c.label}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">URL hình ảnh</label>
                  <input className="form-input" type="url" value={form.imageUrl ?? ''} onChange={e => setForm(f => ({ ...f, imageUrl: e.target.value }))} placeholder="https://..." />
                  <div className="form-hint">Để trống nếu không có hình ảnh</div>
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

      {toast && (
        <div className="toast-container">
          <div className={`toast ${toast.type}`}>{toast.msg}</div>
        </div>
      )}
    </>
  )
}
