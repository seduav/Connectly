export const ROUTES = {
    HOME: '/',
    LOGIN: '/login',
    REGISTER: '/register',

    EXPLORE: '/explore',
    NOTIFICATIONS: '/notifications',
    MESSAGES: '/messages',

    PROFILE: '/profile',
    SETTINGS: '/settings',

    ABOUT: '/about',
    HELP: '/help',
    PRIVACY: '/privacy',
    TERMS: '/terms',
};

export const getProfileRoute = (username) => {
    return `${ROUTES.PROFILE}/${encodeURIComponent(username)}`;
};