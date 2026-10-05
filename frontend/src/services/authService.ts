import api from './api'

interface AuthRequest {
  email: string
  password: string
}

interface AuthResponse {
  token: string
  name: string
  email: string
}

export const authService = {
  register: async (credentials: AuthRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/register', credentials)
    return response.data
  },

  login: async (credentials: AuthRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', credentials)
    return response.data
  },

  getCurrentUser: async () => {
    const response = await api.get('/user/me')
    return response.data
  },
}
