import { useState, useEffect } from 'react'
import { verifyToken } from './api'
import ScenariosPage from './pages/ScenariosPage'
import TopicsPage from './pages/TopicsPage'
import StoriesPage from './pages/StoriesPage'
import MusicPage from './pages/MusicPage'
import LoginPage from './pages/LoginPage'

type Page = 'scenarios' | 'topics' | 'stories' | 'music'

const NAV_ITEMS: { id: Page; label: string; icon: string }[] = [
  { id: 'scenarios', label: 'Bài học cảm xúc', icon: '📚' },
  { id: 'topics', label: 'Chủ đề bài học', icon: '🧩' },
  { id: 'stories', label: 'Câu chuyện', icon: '📖' },
  { id: 'music', label: 'Nhạc thư giãn', icon: '🎵' },
]

export default function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [page, setPage] = useState<Page>('scenarios')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('admin_token')
    if (!token) { setLoading(false); return }
    verifyToken(token)
      .then(ok => {
        setIsLoggedIn(ok)
      })
      .finally(() => {
        setLoading(false)
      })
  }, [])

  if (loading) {
    return (
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}>
        <div className="spinner" style={{ width: 36, height: 36 }} />
      </div>
    )
  }

  if (!isLoggedIn) {
    return <LoginPage onLogin={() => setIsLoggedIn(true)} />
  }

  const PAGE_TITLES: Record<Page, string> = {
    scenarios: 'Bài học cảm xúc',
    topics: 'Chủ đề bài học',
    stories: 'Câu chuyện',
    music: 'Nhạc thư giãn',
  }

  const PAGE_SUBTITLES: Record<Page, string> = {
    scenarios: 'Thêm, sửa, xóa các bài học nhận diện cảm xúc',
    topics: 'Tổ chức bộ câu hỏi theo từng chủ đề từ dễ đến khó',
    stories: 'Quản lý các câu chuyện cảm xúc cho trẻ',
    music: 'Quản lý danh sách nhạc nhẹ thư giãn',
  }

  return (
    <div className="layout">
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="sidebar-header">
          <div className="sidebar-title">😊 Emotion Friend</div>
          <div className="sidebar-sub">Trang quản trị</div>
        </div>
        <nav className="sidebar-nav">
          {NAV_ITEMS.map(item => (
            <button
              key={item.id}
              className={`nav-item${page === item.id ? ' active' : ''}`}
              onClick={() => setPage(item.id)}
            >
              <span className="nav-icon">{item.icon}</span>
              {item.label}
            </button>
          ))}
        </nav>
        <div className="sidebar-footer">
          <button
            className="logout-btn"
            onClick={() => { localStorage.removeItem('admin_token'); setIsLoggedIn(false) }}
          >
            🚪 Đăng xuất
          </button>
        </div>
      </aside>

      {/* Main */}
      <div className="main-content">
        <header className="topbar">
          <div>
            <div className="topbar-title">{PAGE_TITLES[page]}</div>
            <div className="topbar-sub">{PAGE_SUBTITLES[page]}</div>
          </div>
        </header>
        <div className="page-content">
          {page === 'scenarios' && <ScenariosPage />}
          {page === 'topics' && <TopicsPage />}
          {page === 'stories' && <StoriesPage />}
          {page === 'music' && <MusicPage />}
        </div>
      </div>
    </div>
  )
}
