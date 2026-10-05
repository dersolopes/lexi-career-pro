import { Link } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'

function Navbar() {
  const { logout, user } = useAuthStore()

  return (
    <nav className="bg-blue-600 text-white p-4 shadow-md">
      <div className="container mx-auto flex justify-between items-center">
        <Link to="/dashboard" className="text-2xl font-bold">
          LexiCareer Pro
        </Link>
        <div className="flex gap-6 items-center">
          <Link to="/daily-term" className="hover:text-blue-200">
            Termo do Dia
          </Link>
          <Link to="/search" className="hover:text-blue-200">
            Buscar
          </Link>
          <span className="text-sm">{user?.name}</span>
          <button
            onClick={logout}
            className="bg-red-500 px-4 py-2 rounded hover:bg-red-600"
          >
            Sair
          </button>
        </div>
      </div>
    </nav>
  )
}

export default Navbar
