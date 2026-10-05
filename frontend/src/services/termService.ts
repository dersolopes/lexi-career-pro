import api from './api'

export interface Term {
  id: number
  name: string
  definition: string
  simpleExplanation: string
  example: string
  difficultyLevel: string
  categoryId: number
  categoryName: string
  planType: string
}

export interface Category {
  id: number
  name: string
  description: string
  slug: string
  planType: string
  displayOrder: number
}

export interface Dashboard {
  termsLearned: number
  termsToReview: number
  favoriteCount: number
  averageScore: number
  currentLevel: string
}

export const termService = {
  getDailyTerm: async (): Promise<Term> => {
    const response = await api.get<Term>('/terms/daily')
    return response.data
  },

  searchTerms: async (keyword: string): Promise<Term[]> => {
    const response = await api.get<Term[]>('/terms/search', { params: { keyword } })
    return response.data
  },

  getTermById: async (id: number): Promise<Term> => {
    const response = await api.get<Term>(`/terms/${id}`)
    return response.data
  },

  markAsStudied: async (id: number, knowledgeLevel?: string): Promise<void> => {
    await api.post(`/terms/${id}/studied`, null, {
      params: knowledgeLevel ? { knowledgeLevel } : undefined,
    })
  },

  toggleFavorite: async (id: number): Promise<void> => {
    await api.post(`/terms/${id}/favorite`)
  },

  getCategories: async (): Promise<Category[]> => {
    const response = await api.get<Category[]>('/categories')
    return response.data
  },

  getDashboard: async (): Promise<Dashboard> => {
    const response = await api.get<Dashboard>('/dashboard')
    return response.data
  },
}
