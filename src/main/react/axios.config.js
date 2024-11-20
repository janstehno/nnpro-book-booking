import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const restrictedRoutes = [];
    const currentPath = window.location.pathname;

    if (!restrictedRoutes.includes(currentPath) && status >= 400 && status < 600) {
      useNavigate(`/error?status=${status}`);
    }

    return Promise.reject(error);
  }
);

export default api;