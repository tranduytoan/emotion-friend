import { useState } from 'react'
import { verifyToken } from '../api'

export default function LoginPage({ onLogin }: { onLogin: () => void }) {
  const [token, setToken] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!token.trim()) return
    setLoading(true)
    setError('')
    const ok = await verifyToken(token.trim())
    if (ok) {
      localStorage.setItem('admin_token', token.trim())
      onLogin()
    } else {
      setError('Token không hợp lệ. Vui lòng kiểm tra lại.')
    }
    setLoading(false)
  }

  return (
    <div className="login-wrap">
      <div className="login-card">
        <div className="login-emoji">😊</div>
        <div className="login-title">Emotion Friend</div>
        <div className="login-sub">Đăng nhập vào trang quản trị</div>
        {error && <div className="login-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Admin Token</label>
            <input
              className="form-input"
              type="password"
              value={token}
              onChange={e => setToken(e.target.value)}
              placeholder="Nhập admin token..."
              autoFocus
              required
            />
            <div className="form-hint">
              Token được cấu hình bởi biến môi trường ADMIN_TOKEN trên server.<br />
              Mặc định: <code>admin-secret-token</code>
            </div>
          </div>
          <button className="btn btn-primary" style={{ width: '100%', justifyContent: 'center' }} type="submit" disabled={loading}>
            {loading ? <><span className="spinner" /> Đang xác thực...</> : '🔑 Đăng nhập'}
          </button>
        </form>
      </div>
    </div>
  )
}
