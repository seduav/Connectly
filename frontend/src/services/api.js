import axios from 'axios';

import { API_CONFIG } from '../constants/apiConstants';
import { STORAGE_KEYS } from '../constants/storageConstants';

const api = axios.create({
    baseURL: API_CONFIG.BASE_URL,
    timeout: API_CONFIG.TIMEOUT,
    headers: API_CONFIG.HEADERS,
});

// Attach JWT to protected requests.
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem(
            STORAGE_KEYS.AUTH_TOKEN
        );

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
            localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);

            window.dispatchEvent(
                new Event('connectly:unauthorized')
            );
        }

        return Promise.reject(error);
    }
);

export default api;
