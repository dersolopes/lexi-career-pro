import { useState } from 'react'
import { termService, Term } from '../services/termService'

function SearchTerms() {
  const [keyword, setKeyword] = useState('')
  const [results, setResults] = useState<Term[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [searched, setSearched] = useState(false)

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!keyword.trim()) return

    setLoading(true)
    setError('')
    setSearched(true)

    try {
      const data = await termService.searchTerms(keyword)
      setResults(data)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar termos')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container mx-auto p-8">
      <h1 className="text-3xl font-bold mb-8">Buscar Termos</h1>
      
      <form onSubmit={handleSearch} className="max-w-2xl mx-auto mb-8">
        <div className="flex gap-2">
          <input
            type="text"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            placeholder="Digite uma palavra ou conceito..."
            className="flex-1 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            type="submit"
            className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700"
          >
            Buscar
          </button>
        </div>
      </form>
      
      {error && (
        <div className="max-w-2xl mx-auto p-4 bg-red-50 border border-red-200 rounded text-red-700">
          {error}
        </div>
      )}
      
      {searched && !loading && results.length === 0 && !error && (
        <div className="max-w-2xl mx-auto p-4 bg-yellow-50 border border-yellow-200 rounded text-yellow-700">
          Nenhum termo encontrado para "{keyword}"
        </div>
      )}
      
      {loading && (
        <div className="max-w-2xl mx-auto p-4 text-center">Carregando...</div>
      )}
      
      <div className="grid gap-4 max-w-2xl mx-auto">
        {results.map((term) => (
          <div key={term.id} className="bg-white rounded-lg shadow p-6">
            <h3 className="text-xl font-bold text-blue-600 mb-2">{term.name}</h3>
            <p className="text-gray-600 mb-3">{term.simpleExplanation}</p>
            <div className="flex gap-2">
              <span className="bg-blue-100 text-blue-800 px-2 py-1 rounded text-sm">
                {term.categoryName}
              </span>
              <span className="bg-gray-100 text-gray-800 px-2 py-1 rounded text-sm">
                {term.difficultyLevel}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default SearchTerms
