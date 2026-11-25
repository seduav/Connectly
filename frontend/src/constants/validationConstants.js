export const VALIDATION = {
    USERNAME: {
        MIN_LENGTH: 3,
        MAX_LENGTH: 30,
    },

    PASSWORD: {
        MIN_LENGTH: 8,
        MAX_LENGTH: 72,
    },

    EMAIL: {
        MAX_LENGTH: 100,
    },

    NAME: {
        MIN_LENGTH: 1,
        MAX_LENGTH: 50,
    },

    BIO: {
        MAX_LENGTH: 500,
    },

    POST: {
        MIN_LENGTH: 1,
        MAX_LENGTH: 5000,
    },

    COMMENT: {
        MIN_LENGTH: 1,
        MAX_LENGTH: 1000,
    },

    PROFILE_PICTURE_URL: {
        MAX_LENGTH: 500,
    },
};

export const VALIDATION_PATTERNS = {
    HTTP_URL: /^https?:\/\/.+/i,
};