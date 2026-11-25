export const ERROR_MESSAGES = {
    // Authentication
    INVALID_CREDENTIALS: 'Invalid email or password.',
    LOGIN_FAILED: 'Unable to login. Please try again.',
    REGISTRATION_FAILED:
        'Unable to create your account. Please try again.',
    AUTHENTICATION_FAILED: 'Authentication failed.',

    // Posts
    POST_EMPTY: 'Please write something before publishing.',
    POST_CREATE_FAILED:
        'Unable to publish your post. Please try again.',
    POST_UPDATE_FAILED: 'Unable to update your post.',
    POST_DELETE_FAILED: 'Unable to delete your post.',
    POST_LOAD_FAILED:
        'Unable to load posts. Please try again.',

    // Comments
    COMMENT_EMPTY: 'Please write a comment.',
    COMMENT_CREATE_FAILED: 'Unable to publish your comment.',
    COMMENT_UPDATE_FAILED: 'Unable to update comment.',
    COMMENT_DELETE_FAILED: 'Unable to delete comment.',
    COMMENT_LOAD_FAILED: 'Unable to load comments.',

    // Profile
    PROFILE_UPDATE_FAILED: 'Failed to update your profile.',
    FIRST_NAME_REQUIRED: 'First name is required.',
    LAST_NAME_REQUIRED: 'Last name is required.',
    INVALID_PROFILE_PICTURE_URL:
        'Profile picture URL must start with http:// or https://.',

    // General
    SOMETHING_WENT_WRONG:
        'Something went wrong. Please try again.',
    UNAUTHORIZED: 'You are not authorized to perform this action.',
};

export const SUCCESS_MESSAGES = {
    ACCOUNT_CREATED: 'Account created successfully!',
    PROFILE_UPDATED: 'Profile updated successfully!',
    POST_CREATED: 'Post published successfully!',
    POST_UPDATED: 'Post updated successfully!',
    COMMENT_CREATED: 'Comment published successfully!',
};

export const CONFIRM_MESSAGES = {
    LOGOUT: 'Are you sure you want to log out?',
    DELETE_POST: 'Are you sure you want to delete this post?',
    DELETE_COMMENT: 'Delete this comment?',
};

export const UI_MESSAGES = {
    LOADING: 'Loading Connectly...',
    LOADING_POSTS: 'Loading your community...',
    NO_POSTS: 'There are no posts yet.',
    NO_SUGGESTIONS: 'No suggestions available.',
    NO_CONVERSATIONS: 'No conversations yet.',
    NO_NOTIFICATIONS: "You're all caught up!",
    USER_NOT_FOUND: 'This profile doesn\'t exist.',
};