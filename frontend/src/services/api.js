import axios from 'axios';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Attach JWT to protected requests.
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('connectly_token');

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

// Centralized response error handling.
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('connectly_token');

            // Authentication context will handle session state.
            window.dispatchEvent(new Event('connectly:unauthorized'));
        }

        return Promise.reject(error);
    }
);

export default api;
