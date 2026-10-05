import { useEffect, useState } from 'react'
import { termService, Term } from '../services/termService'

function DailyTerm() {
  const [term, setTerm] = useState<Term | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [studied, setStudied] = useState(false)

  useEffect(() => {
    const fetchDailyTerm = async () => {
      try {
        const data = await termService.getDailyTerm()
        setTerm(data)
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erro ao carregar termo do dia')
      } finally {
        setLoading(false)
      }
    }

    fetchDailyTerm()
  }, [])

  const handleMarkAsStudied = async (knowledgeLevel: string) => {
    try {
      if (term) {
        await termService.markAsStudied(term.id, knowledgeLevel)
        setStudied(true)
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao marcar como estudado')
    }
  }

  const handleToggleFavorite = async () => {
    try {
      if (term) {
        await termService.toggleFavorite(term.id)
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao adicionar aos favoritos')
    }
  }

  if (loading) {
    return <div className="flex justify-center items-center min-h-screen">Carregando...</div>
  }

  if (error) {
    return <div className="flex justify-center items-center min-h-screen text-red-500">{error}</div>
  }

  return (
    <div className="container mx-auto p-8">
      <h1 className="text-3xl font-bold mb-8">Seu Termo de Hoje</h1>
      
      {term && (
        <div className="bg-white rounded-lg shadow-lg p-8 max-w-2xl mx-auto">
          <div className="flex justify-between items-start mb-6">
            <h2 className="text-4xl font-bold text-blue-600">{term.name}</h2>
            <button
              onClick={handleToggleFavorite}
              className="text-2xl cursor-pointer"
              title="Adicionar aos favoritos"
            >
              ⭐
            </button>
          </div>
          
          <div className="mb-6">
            <span className="inline-block bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-medium">
              {term.categoryName}
            </span>
            <span className="ml-2 inline-block bg-gray-100 text-gray-800 px-3 py-1 rounded-full text-sm font-medium">
              {term.difficultyLevel}
            </span>
          </div>
          
          <div className="space-y-6">
            <div>
              <h3 className="text-lg font-semibold text-gray-700 mb-2">O que significa?</h3>
              <p className="text-gray-600">{term.definition}</p>
            </div>
            
            <div>
              <h3 className="text-lg font-semibold text-gray-700 mb-2">Explicando de forma simples</h3>
              <p className="text-gray-600">{term.simpleExplanation}</p>
            </div>
            
            <div>
              <h3 className="text-lg font-semibold text-gray-700 mb-2">Na prática</h3>
              <p className="text-gray-600 italic">" {term.example} "</p>
            </div>
          </div>
          
          {!studied && (
            <div className="mt-8 space-y-3">
              <h3 className="text-lg font-semibold text-gray-700">Você já conhecia esse termo?</h3>
              <div className="grid grid-cols-2 gap-3">
                <button
                  onClick={() => handleMarkAsStudied('NEVER_SEEN')}
                  className="bg-red-500 text-white py-2 rounded hover:bg-red-600"
                >
                  Nunca vi
                </button>
                <button
                  onClick={() => handleMarkAsStudied('HEARD_NOT_EXPLAINED')}
                  className="bg-yellow-500 text-white py-2 rounded hover:bg-yellow-600"
                >
                  Já ouvi
                </button>
                <button
                  onClick={() => handleMarkAsStudied('KNOWN')}
                  className="bg-blue-500 text-white py-2 rounded hover:bg-blue-600"
                >
                  Conheço
                </button>
                <button
                  onClick={() => handleMarkAsStudied('USE_IN_WORK')}
                  className="bg-green-500 text-white py-2 rounded hover:bg-green-600"
                >
                  Uso no trabalho
                </button>
              </div>
            </div>
          )}
          
          {studied && (
            <div className="mt-8 p-4 bg-green-50 border border-green-200 rounded">
              <p className="text-green-700 font-semibold">✓ Termo marcado como estudado!</p>
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default DailyTerm
