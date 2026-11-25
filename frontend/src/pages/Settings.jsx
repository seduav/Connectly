import { useEffect, useState } from 'react';

import { useAuth } from '../hooks/useAuth';
import { useOutletContext } from 'react-router-dom';

import {
    THEMES,
    USER_ROLES,
    ACCOUNT_STATUS,
} from '../constants/appConstants';

import {
    VALIDATION,
    VALIDATION_PATTERNS,
} from '../constants/validationConstants';

import {
    ERROR_MESSAGES,
    SUCCESS_MESSAGES,
} from '../constants/messages';

function createInitialFormData(user) {
    return {
        firstName: user?.firstName || '',
        lastName: user?.lastName || '',
        bio: user?.profile?.bio || user?.bio || '',
        profilePictureUrl:
            user?.profile?.profilePictureUrl ||
            user?.profilePictureUrl || '',
        dateOfBirth:
            user?.profile?.dateOfBirth ||
            user?.dateOfBirth || '',
    };
}

function ProfileSettingsForm({ user, updateProfile }) {
    const [formData, setFormData] = useState(
        () => createInitialFormData(user)
    );

    const [saving, setSaving] = useState(false);
    const [success, setSuccess] = useState('');
    const [error, setError] = useState('');

    function handleChange(event) {
        const { name, value } = event.target;

        setFormData((previous) => ({
            ...previous,
            [name]: value,
        }));
    }

    async function handleSaveProfile(event) {
        event.preventDefault();

        setSaving(true);
        setSuccess('');
        setError('');

        if (!formData.firstName.trim()) {
            setError(ERROR_MESSAGES.FIRST_NAME_REQUIRED);
            setSaving(false);
            return;
        }

        if (!formData.lastName.trim()) {
            setError(ERROR_MESSAGES.LAST_NAME_REQUIRED);
            setSaving(false);
            return;
        }

        if (
            formData.profilePictureUrl &&
            !VALIDATION_PATTERNS.HTTP_URL.test(
                formData.profilePictureUrl
            )
        ) {
            setError(ERROR_MESSAGES.INVALID_PROFILE_PICTURE_URL);
            setSaving(false);
            return;
        }

        try {
            await updateProfile({
                firstName: formData.firstName.trim(),
                lastName: formData.lastName.trim(),
                bio: formData.bio,
                profilePictureUrl: formData.profilePictureUrl,
                dateOfBirth: formData.dateOfBirth || null,
            });

            setSuccess(SUCCESS_MESSAGES.PROFILE_UPDATED);
        } catch (err) {
            setError(
                err.message ||
                ERROR_MESSAGES.PROFILE_UPDATE_FAILED
            );
        } finally {
            setSaving(false);
        }
    }

    return (
        <section className="stories-section">
            <div className="section-heading">
                <div>
                    <h2>Profile information</h2>
                    <p>Update your personal information.</p>
                </div>
            </div>

            <form onSubmit={handleSaveProfile}>
                <div className="post-edit-form">
                    <label htmlFor="firstName">First name</label>

                    <input
                        id="firstName"
                        className="comment-edit-input"
                        type="text"
                        name="firstName"
                        value={formData.firstName}
                        onChange={handleChange}
                        maxLength={VALIDATION.NAME.MAX_LENGTH}
                        required
                        disabled={saving}
                    />

                    <label htmlFor="lastName">Last name</label>

                    <input
                        id="lastName"
                        className="comment-edit-input"
                        type="text"
                        name="lastName"
                        value={formData.lastName}
                        onChange={handleChange}
                        maxLength={VALIDATION.NAME.MAX_LENGTH}
                        required
                        disabled={saving}
                    />

                    <label htmlFor="bio">Bio</label>

                    <textarea
                        id="bio"
                        className="create-post-textarea"
                        name="bio"
                        value={formData.bio}
                        onChange={handleChange}
                        maxLength={VALIDATION.BIO.MAX_LENGTH}
                        placeholder="Tell people about yourself..."
                        disabled={saving}
                    />

                    <span className="character-counter">
                        {formData.bio.length}/
                        {VALIDATION.BIO.MAX_LENGTH} characters
                    </span>

                    <label htmlFor="profilePictureUrl">
                        Profile picture URL
                    </label>

                    <input
                        id="profilePictureUrl"
                        className="comment-edit-input"
                        type="url"
                        name="profilePictureUrl"
                        value={formData.profilePictureUrl}
                        onChange={handleChange}
                        maxLength={
                            VALIDATION.PROFILE_PICTURE_URL.MAX_LENGTH
                        }
                        placeholder="https://example.com/photo.jpg"
                        disabled={saving}
                    />

                    <label htmlFor="dateOfBirth">
                        Date of birth
                    </label>

                    <input
                        id="dateOfBirth"
                        className="comment-edit-input"
                        type="date"
                        name="dateOfBirth"
                        value={formData.dateOfBirth}
                        onChange={handleChange}
                        max={new Date().toISOString().split('T')[0]}
                        disabled={saving}
                    />
                </div>

                {error && (
                    <p className="interaction-error" role="alert">
                        {error}
                    </p>
                )}

                {success && (
                    <p className="comments-status" role="status">
                        ✓ {success}
                    </p>
                )}

                <button
                    type="submit"
                    className="modal-submit"
                    disabled={saving}
                >
                    {saving
                        ? 'Saving changes...'
                        : 'Save profile changes'}
                </button>
            </form>
        </section>
    );
}

function Settings() {
    const { user, updateProfile } = useAuth();

    const { darkMode, setDarkMode } = useOutletContext();

    useEffect(() => {
        document.title = 'Settings | Connectly';
    }, []);

    return (
        <div>
            <div className="welcome-heading">
                <div>
                    <span className="welcome-label">
                        PERSONALIZATION
                    </span>

                    <h1>Settings</h1>

                    <p>
                        Manage your account and customize
                        your Connectly experience.
                    </p>
                </div>
            </div>

            <div className="posts-list">
                {/* PROFILE SETTINGS */}

                <ProfileSettingsForm
                    key={user?.id || user?.username || 'profile'}
                    user={user}
                    updateProfile={updateProfile}
                />

                {/* ACCOUNT SETTINGS */}

                <section className="stories-section">
                    <div className="section-heading">
                        <div>
                            <h2>Account information</h2>

                            <p>
                                View your Connectly account details.
                            </p>
                        </div>
                    </div>

                    <div className="post-edit-form">
                        <label>Username</label>

                        <input
                            className="comment-edit-input"
                            type="text"
                            value={user?.username || ''}
                            readOnly
                        />

                        <label>Email address</label>

                        <input
                            className="comment-edit-input"
                            type="email"
                            value={user?.email || ''}
                            readOnly
                        />

                        <label>Account role</label>

                        <input
                            className="comment-edit-input"
                            type="text"
                            value={user?.role || USER_ROLES.USER}
                            readOnly
                        />

                        <label>Account status</label>

                        <input
                            className="comment-edit-input"
                            type="text"
                            value={
                                user?.enabled
                                    ? ACCOUNT_STATUS.ACTIVE
                                    : ACCOUNT_STATUS.DISABLED
                            }
                            readOnly
                        />

                        <label>Member since</label>

                        <input
                            className="comment-edit-input"
                            type="text"
                            value={
                                user?.createdAt
                                    ? new Date(
                                        user.createdAt
                                    ).toLocaleDateString()
                                    : 'Not available'
                            }
                            readOnly
                        />
                    </div>
                </section>

                {/* APPEARANCE SETTINGS */}

                <section className="stories-section">
                    <div className="section-heading">
                        <div>
                            <h2>Appearance</h2>

                            <p>
                                Customize your Connectly experience.
                            </p>
                        </div>
                    </div>

                    <div className="suggestions-list">
                        <div className="suggestion">
                            <div className="suggestion-info">
                                <strong>Dark mode</strong>

                                <span>
                                    Change between light and dark themes.
                                </span>
                            </div>

                            <button
                                type="button"
                                className="filter-button"
                                onClick={() =>
                                    setDarkMode((current) => !current)
                                }
                            >
                                {darkMode
                                    ? 'Light mode'
                                    : 'Dark mode'}
                            </button>
                        </div>

                        <div className="suggestion">
                            <div className="suggestion-info">
                                <strong>Current theme</strong>

                                <span>
                                    {darkMode
                                        ? THEMES.DARK
                                        : THEMES.LIGHT}
                                </span>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    );
}

export default Settings;
