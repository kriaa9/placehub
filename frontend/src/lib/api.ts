import axios, { InternalAxiosRequestConfig } from "axios"

const api = axios.create({
  baseURL: "http://localhost:8080/api",
})

// Add JWT token to requests
api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem("token")
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default api
;
