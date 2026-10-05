import { useEffect, useState } from 'react'
import { termService, Dashboard as DashboardData } from '../services/termService'

function Dashboard() {
  const [dashboard, setDashboard] = useState<DashboardData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const data = await termService.getDashboard()
        setDashboard(data)
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erro ao carregar dashboard')
      } finally {
        setLoading(false)
      }
    }

    fetchDashboard()
  }, [])

  if (loading) {
    return <div className="flex justify-center items-center min-h-screen">Carregando...</div>
  }

  if (error) {
    return <div className="flex justify-center items-center min-h-screen text-red-500">{error}</div>
  }

  return (
    <div className="container mx-auto p-8">
      <h1 className="text-3xl font-bold mb-8">Dashboard</h1>
      
      {dashboard && (
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="bg-blue-50 p-6 rounded-lg">
            <h3 className="text-gray-600 text-sm font-medium">Termos Aprendidos</h3>
            <p className="text-3xl font-bold text-blue-600">{dashboard.termsLearned}</p>
          </div>
          
          <div className="bg-yellow-50 p-6 rounded-lg">
            <h3 className="text-gray-600 text-sm font-medium">Para Revisar</h3>
            <p className="text-3xl font-bold text-yellow-600">{dashboard.termsToReview}</p>
          </div>
          
          <div className="bg-green-50 p-6 rounded-lg">
            <h3 className="text-gray-600 text-sm font-medium">Favoritos</h3>
            <p className="text-3xl font-bold text-green-600">{dashboard.favoriteCount}</p>
          </div>
          
          <div className="bg-purple-50 p-6 rounded-lg">
            <h3 className="text-gray-600 text-sm font-medium">Taxa de Acerto</h3>
            <p className="text-3xl font-bold text-purple-600">{dashboard.averageScore.toFixed(1)}%</p>
          </div>
        </div>
      )}
      
      <div className="mt-8 p-6 bg-gray-50 rounded-lg">
        <h2 className="text-xl font-bold mb-2">Bem-vindo ao LexiCareer Pro!</h2>
        <p className="text-gray-600">
          Comece seu aprendizado diário. Acesse o "Termo do Dia" para receber seu primeiro conceito profissional.
        </p>
      </div>
    </div>
  )
}

export default Dashboard
