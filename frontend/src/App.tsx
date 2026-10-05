import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from './store/authStore'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import DailyTerm from './pages/DailyTerm'
import SearchTerms from './pages/SearchTerms'
import Navbar from './components/Navbar'

function App() {
  const { token } = useAuthStore()

  return (
    <BrowserRouter>
      {token && <Navbar />}
      <Routes>
        {!token ? (
          <>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="*" element={<Navigate to="/login" />} />
          </>
        ) : (
          <>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/daily-term" element={<DailyTerm />} />
            <Route path="/search" element={<SearchTerms />} />
            <Route path="*" element={<Navigate to="/dashboard" />} />
          </>
        )}
      </Routes>
    </BrowserRouter>
  )
}

export default App
