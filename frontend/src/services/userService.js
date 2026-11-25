import api from './api';

import { API_ENDPOINTS } from '../constants/apiConstants';

const userService = {
    async getMyProfile() {
        const response = await api.get(
            API_ENDPOINTS.MY_PROFILE
        );

        return response.data;
    },

    async updateMyProfile(profileData) {
        const response = await api.put(
            API_ENDPOINTS.MY_PROFILE,
            profileData
        );

        return response.data;
    },

    async getSuggestions() {
        const response = await api.get(
            API_ENDPOINTS.USER_SUGGESTIONS
        );

        return response.data;
    },
};

export default userService;
