import api from './api';

import { STORAGE_KEYS } from '../constants/storageConstants';
import { API_ENDPOINTS } from '../constants/apiConstants';
import { ERROR_MESSAGES } from '../constants/messages';

const authService = {
    async register(userData) {
        const response = await api.post(
            API_ENDPOINTS.REGISTER,
            userData
        );

        return response.data;
    },

    async login(credentials) {
        const response = await api.post(
            API_ENDPOINTS.LOGIN,
            credentials
        );

        const { accessToken } = response.data;

        if (!accessToken) {
            throw new Error(
                ERROR_MESSAGES.AUTHENTICATION_FAILED
            );
        }

        localStorage.setItem(
            STORAGE_KEYS.AUTH_TOKEN,
            accessToken
        );

        return response.data;
    },

    async getCurrentUser() {
        const response = await api.get(
            API_ENDPOINTS.CURRENT_USER
        );

        return response.data;
    },

    async getProfile() {
        const response = await api.get(
            API_ENDPOINTS.MY_PROFILE
        );

        return response.data;
    },

    async updateProfile(profileData) {
        const response = await api.put(
            API_ENDPOINTS.MY_PROFILE,
            profileData
        );

        return response.data;
    },

    logout() {
        localStorage.removeItem(
            STORAGE_KEYS.AUTH_TOKEN
        );
    },
};

export default authService;
