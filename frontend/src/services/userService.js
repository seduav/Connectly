import api from './api';

const userService = {

    async getMyProfile() {
        const response = await api.get('/users/me/profile');
        return response.data;
    },

    async updateMyProfile(profileData) {
        const response = await api.put(
            '/users/me/profile',
            profileData
        );

        return response.data;
    },

    async getSuggestions() {
        const response = await api.get('/users/suggestions');
        return response.data;
    },
};

export default userService;