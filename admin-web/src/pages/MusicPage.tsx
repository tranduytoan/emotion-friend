import { useState, useEffect, useCallback } from 'react'
import { musicApi, MusicTrack } from '../api'

type FormData = Omit<MusicTrack, 'id'> & { id?: string }

const EMPTY_FORM: FormData = { title: '', artist: '', filename: '', sortOrder: 0 }

export default function MusicPage() {
  const [items, setItems] = useState<MusicTrack[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [modal, setModal] = useState<{ open: boolean; editing?: MusicTrack }>({ open: false })
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
      setItems(await musicApi.list())
      setError('')
    } catch (e) {
      setError(String(e))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { load() }, [load])

  function openCreate() { setForm(EMPTY_FORM); setModal({ open: true }) }
  function openEdit(item: MusicTrack) { setForm({ ...item }); setModal({ open: true, editing: item }) }

  async function handleSave(e: React.FormEvent) {
    e.preventDefault()
    setSaving(true)
    try {
      if (modal.editing) {
        await musicApi.update(modal.editing.id, form as Omit<MusicTrack, 'id'>)
        showToast('Đã cập nhật bài nhạc!')
      } else {
        await musicApi.create(form)
        showToast('Đã thêm bài nhạc mới!')
      }
      setModal({ open: false })
      load()
    } catch (e) {
      showToast(String(e), 'error')
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(item: MusicTrack) {
    if (!confirm(`Xóa bài nhạc "${item.title}"?`)) return
    try {
      await musicApi.delete(item.id)
      showToast('Đã xóa!')
      load()
    } catch (e) {
      showToast(String(e), 'error')
    }
  }

  return (
    <>
      <div className="card">
        <div className="card-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <span className="card-title">Danh sách nhạc thư giãn</span>
            <span className="card-count">{items.length} bài</span>
          </div>
          <button className="btn btn-primary" onClick={openCreate}>+ Thêm bài nhạc</button>
        </div>
        <div className="table-wrap">
          {loading ? (
            <div className="empty-state"><div className="spinner" style={{ width: 30, height: 30, margin: '0 auto' }} /></div>
          ) : error ? (
            <div className="empty-state"><div className="empty-icon">⚠️</div><div className="empty-text">{error}</div></div>
          ) : items.length === 0 ? (
            <div className="empty-state"><div className="empty-icon">🎵</div><div className="empty-text">Chưa có bài nhạc nào</div></div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>#</th>
                  <th>Tên bài</th>
                  <th>Nghệ sĩ</th>
                  <th>Tên file</th>
                  <th>Thứ tự</th>
                  <th>Hành động</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item, i) => (
                  <tr key={item.id}>
                    <td style={{ color: '#94a3b8' }}>{i + 1}</td>
                    <td><strong>🎵 {item.title}</strong></td>
                    <td>{item.artist || <span style={{ color: '#94a3b8' }}>—</span>}</td>
                    <td><span className="badge badge-orange">{item.filename}</span></td>
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
              <span className="modal-title">{modal.editing ? 'Sửa bài nhạc' : 'Thêm bài nhạc mới'}</span>
              <button className="modal-close" onClick={() => setModal({ open: false })}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Tên bài *</label>
                  <input className="form-input" value={form.title} onChange={e => setForm(f => ({ ...f, title: e.target.value }))} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Nghệ sĩ</label>
                  <input className="form-input" value={form.artist} onChange={e => setForm(f => ({ ...f, artist: e.target.value }))} placeholder="Để trống nếu không có" />
                </div>
                <div className="form-group">
                  <label className="form-label">Tên file *</label>
                  <input className="form-input" value={form.filename} onChange={e => setForm(f => ({ ...f, filename: e.target.value }))} required placeholder="vd: soft_music_1" />
                  <div className="form-hint">
                    Tên file MP3 trong thư mục <code>res/raw/</code> của app Android (không cần đuôi .mp3).<br />
                    Ví dụ: <code>soft_music_1</code>, <code>soft_music_2</code>, ...
                  </div>
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
