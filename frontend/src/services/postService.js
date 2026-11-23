import api from './api';

const postService = {
    async getAllPosts() {
        const response = await api.get('/posts');
        return response.data;
    },

    async getPostById(postId) {
        const response = await api.get(`/posts/${postId}`);
        return response.data;
    },

    async getPostsByUser(userId) {
        const response = await api.get(`/posts/user/${userId}`);
        return response.data;
    },

    async createPost(content) {
        const response = await api.post('/posts', { content });
        return response.data;
    },

    async updatePost(postId, content) {
        const response = await api.put(
            `/posts/${postId}`,
            { content }
        );

        return response.data;
    },

    async deletePost(postId) {
        await api.delete(`/posts/${postId}`);
    },
};

export default postService;
