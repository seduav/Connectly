import api from './api';

import { API_ENDPOINTS } from '../constants/apiConstants';

const postService = {
    async getAllPosts() {
        const response = await api.get(
            API_ENDPOINTS.POSTS
        );

        return response.data;
    },

    async getPostById(postId) {
        const response = await api.get(
            `${API_ENDPOINTS.POSTS}/${postId}`
        );

        return response.data;
    },

    async getPostsByUser(userId) {
        const response = await api.get(
            `${API_ENDPOINTS.POSTS}/user/${userId}`
        );

        return response.data;
    },

    async createPost(content) {
        const response = await api.post(
            API_ENDPOINTS.POSTS,
            { content }
        );

        return response.data;
    },

    async updatePost(postId, content) {
        const response = await api.put(
            `${API_ENDPOINTS.POSTS}/${postId}`,
            { content }
        );

        return response.data;
    },

    async deletePost(postId) {
        await api.delete(
            `${API_ENDPOINTS.POSTS}/${postId}`
        );
    },
};

export default postService;
