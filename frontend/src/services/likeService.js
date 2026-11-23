import api from './api';

const likeService = {
    async toggleLike(postId) {
        const response = await api.post(
            `/posts/${postId}/likes`
        );

        return response.data;
    },

    async getLikesCount(postId) {
        const response = await api.get(
            `/posts/${postId}/likes/count`
        );

        return response.data;
    },

    async hasUserLikedPost(postId) {
        const response = await api.get(
            `/posts/${postId}/likes/me`
        );

        return response.data;
    },
};

export default likeService;
