export const API_CONFIG = {
    BASE_URL:
        import.meta.env.VITE_API_BASE_URL ||
        'http://localhost:8080/api',

    TIMEOUT: 10000,

    HEADERS: {
        'Content-Type': 'application/json',
    },
};

export const API_ENDPOINTS = {
    // Authentication
    LOGIN: '/auth/login',

    // Users
    REGISTER: '/users/register',
    CURRENT_USER: '/users/me',
    MY_PROFILE: '/users/me/profile',
    USER_SUGGESTIONS: '/users/suggestions',

    // Posts
    POSTS: '/posts',

    // Comments
    COMMENTS: '/comments',

    // Likes
    LIKES: '/likes',
};