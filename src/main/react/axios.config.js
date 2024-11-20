import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const setupAxiosInterceptors = (addError) => {
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
      if (error.response) {
        addError({
          message: error.response.data?.message || 'Server communication error.',
          status: error.response.status,
        });
      } else if (error.request) {
        addError({ message: 'Server is not responding.' });
      } else {
        addError({ message: error.message });
      }
      return Promise.reject(error);
    }
  );
};

export default api;