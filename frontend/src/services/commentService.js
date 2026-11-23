import api from './api';

const commentService = {
    // Create a comment on a post.
    async createComment(postId, content) {
        const response = await api.post(
            `/posts/${postId}/comments`,
            { content }
        );

        return response.data;
    },

    // Get all comments belonging to a post.
    async getCommentsByPost(postId) {
        const response = await api.get(
            `/posts/${postId}/comments`
        );

        return response.data;
    },

    // Get a specific comment within a post.
    async getCommentById(postId, commentId) {
        const response = await api.get(
            `/posts/${postId}/comments/${commentId}`
        );

        return response.data;
    },

    // Update an existing comment.
    async updateComment(postId, commentId, content) {
        const response = await api.put(
            `/posts/${postId}/comments/${commentId}`,
            { content }
        );

        return response.data;
    },

    // Delete a comment.
    async deleteComment(postId, commentId) {
        await api.delete(
            `/posts/${postId}/comments/${commentId}`
        );
    },
};

export default commentService;
