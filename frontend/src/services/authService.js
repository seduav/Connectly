import api from './api';

const authService = {
    async register(userData) {
        const response = await api.post(
            '/users/register',
            userData
        );

        return response.data;
    },

    async login(credentials) {
        const response = await api.post(
            '/auth/login',
            credentials
        );

        const { accessToken } = response.data;

        localStorage.setItem(
            'connectly_token',
            accessToken
        );

        return response.data;
    },

    // Retrieve complete authenticated user information.
    async getCurrentUser() {
        const response = await api.get('/users/me');

        return response.data;
    },

    // Retrieve profile information.
    async getProfile() {
        const response = await api.get(
            '/users/me/profile'
        );

        return response.data;
    },

    // Update profile information.
    async updateProfile(profileData) {
        const response = await api.put(
            '/users/me/profile',
            profileData
        );

        return response.data;
    },

    logout() {
        localStorage.removeItem('connectly_token');
    },
};

export default authService;
