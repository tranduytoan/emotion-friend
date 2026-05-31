import { useCallback, useEffect, useState } from 'react'
import { LessonTopic, topicsApi } from '../api'

type FormData = Omit<LessonTopic, 'id'>

const EMPTY_FORM: FormData = {
  title: '',
  description: '',
  difficulty: 1,
  sortOrder: 0,
}

export default function TopicsPage() {
  const [items, setItems] = useState<LessonTopic[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [modal, setModal] = useState<{ open: boolean; editing?: LessonTopic }>({ open: false })
  const [form, setForm] = useState<FormData>(EMPTY_FORM)
  const [saving, setSaving] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      setItems(await topicsApi.list())
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

  function openEdit(item: LessonTopic) {
    setForm({
      title: item.title,
      description: item.description,
      difficulty: item.difficulty,
      sortOrder: item.sortOrder,
    })
    setModal({ open: true, editing: item })
  }

  async function handleSave(e: React.FormEvent) {
    e.preventDefault()
    setSaving(true)
    try {
      if (modal.editing) {
        await topicsApi.update(modal.editing.id, form)
      } else {
        await topicsApi.create(form)
      }
      setModal({ open: false })
      await load()
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(item: LessonTopic) {
    if (!confirm(`Xóa chủ đề "${item.title}"?`)) return
    await topicsApi.delete(item.id)
    await load()
  }

  return (
    <>
      <div className="card">
        <div className="card-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <span className="card-title">Danh sách chủ đề</span>
            <span className="card-count">{items.length} chủ đề</span>
          </div>
          <button className="btn btn-primary" onClick={openCreate}>+ Thêm chủ đề</button>
        </div>
        <div className="table-wrap">
          {loading ? (
            <div className="empty-state"><div className="spinner" style={{ width: 30, height: 30, margin: '0 auto' }} /></div>
          ) : error ? (
            <div className="empty-state"><div className="empty-icon">⚠️</div><div className="empty-text">{error}</div></div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>#</th>
                  <th>Tên chủ đề</th>
                  <th>Mô tả</th>
                  <th>Độ khó</th>
                  <th>Thứ tự</th>
                  <th>Hành động</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item, i) => (
                  <tr key={item.id}>
                    <td>{i + 1}</td>
                    <td><strong>{item.title}</strong></td>
                    <td className="td-truncate">{item.description}</td>
                    <td>{item.difficulty}</td>
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
              <span className="modal-title">{modal.editing ? 'Sửa chủ đề' : 'Thêm chủ đề mới'}</span>
              <button className="modal-close" onClick={() => setModal({ open: false })}>×</button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Tên chủ đề *</label>
                  <input className="form-input" value={form.title} onChange={e => setForm(f => ({ ...f, title: e.target.value }))} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Mô tả</label>
                  <textarea className="form-textarea" value={form.description} onChange={e => setForm(f => ({ ...f, description: e.target.value }))} rows={3} />
                </div>
                <div className="form-group">
                  <label className="form-label">Độ khó</label>
                  <select className="form-select" value={form.difficulty} onChange={e => setForm(f => ({ ...f, difficulty: Number(e.target.value) }))}>
                    <option value={1}>1 - Dễ</option>
                    <option value={2}>2 - Trung bình</option>
                    <option value={3}>3 - Khó</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Thứ tự</label>
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
    </>
  )
}
