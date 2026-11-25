import { useState } from 'react';
import { Link } from 'react-router-dom';

import Avatar from '../common/Avatar';

import {
    ROUTES,
    getProfileRoute,
} from '../../constants/routeConstants';

import { UI_MESSAGES } from '../../constants/messages';
import { APP_CONFIG } from '../../constants/appConstants';

function RightSidebar({ user, suggestions = [] }) {
    const [following, setFollowing] = useState([]);

    function toggleFollow(username) {
        setFollowing((current) =>
            current.includes(username)
                ? current.filter((name) => name !== username)
                : [...current, username]
        );
    }

    return (
        <aside className="right-sidebar">
            <Link
                to={getProfileRoute(user?.username || '')}
                className="current-user"
                style={{
                    textDecoration: 'none',
                    color: 'inherit',
                    cursor: 'pointer',
                }}
            >
                <Avatar
                    initials={
                        `${user?.firstName?.[0] || ''}${user?.lastName?.[0] || ''}`
                            .toUpperCase() ||
                        APP_CONFIG.DEFAULT_AVATAR_INITIALS
                    }
                    color="lavender"
                    size="large"
                />

                <div className="current-user-info">
                    <strong>{user?.username}</strong>

                    <span>
                        {user?.firstName} {user?.lastName}
                    </span>
                </div>
            </Link>

            <div className="suggestions-heading">
                <span>Suggested for you</span>

                <Link to={ROUTES.EXPLORE} className="text-button">
                    See all
                </Link>
            </div>

            <div className="suggestions-list">
                {suggestions.map((suggestedUser) => (
                    <div
                        className="suggestion"
                        key={suggestedUser.username}
                    >
                        <Link
                            to={getProfileRoute(suggestedUser.username)}
                            className="suggestion-profile"
                            style={{
                                display: 'flex',
                                alignItems: 'center',
                                gap: '12px',
                                textDecoration: 'none',
                                color: 'inherit',
                                flex: 1,
                                minWidth: 0,
                            }}
                        >
                            <Avatar
                                initials={
                                    suggestedUser.initials ||
                                    `${suggestedUser.firstName?.[0] || ''}${suggestedUser.lastName?.[0] || ''}`
                                        .toUpperCase() ||
                                    APP_CONFIG.DEFAULT_AVATAR_INITIALS
                                }
                                color="lavender"
                            />

                            <div className="suggestion-info">
                                <strong>
                                    {suggestedUser.username}
                                </strong>

                                <span>
                                    {suggestedUser.firstName}{' '}
                                    {suggestedUser.lastName}
                                </span>
                            </div>
                        </Link>

                        <button
                            className="follow-button"
                            onClick={() =>
                                toggleFollow(suggestedUser.username)
                            }
                        >
                            {following.includes(suggestedUser.username)
                                ? 'Following'
                                : 'Follow'}
                        </button>
                    </div>
                ))}

                {suggestions.length === 0 && (
                    <p>{UI_MESSAGES.NO_SUGGESTIONS}</p>
                )}
            </div>

            <div className="community-card">
                <div className="community-icon">✦</div>

                <h3>Good things grow together.</h3>

                <p>
                    Share your moments, find your people and make
                    new connections.
                </p>
            </div>

            <footer className="right-footer">
                <div
                    style={{
                        display: 'flex',
                        flexWrap: 'wrap',
                        gap: '5px 10px',
                    }}
                >
                    <Link to={ROUTES.ABOUT}>About</Link>
                    <Link to={ROUTES.HELP}>Help</Link>
                    <Link to={ROUTES.PRIVACY}>Privacy</Link>
                    <Link to={ROUTES.TERMS}>Terms</Link>
                </div>

                <div style={{ marginTop: '10px' }}>
                    Made with ♡
                    <br />
                    {APP_CONFIG.NAME.toUpperCase()} © {APP_CONFIG.COPYRIGHT_YEAR}
                </div>
            </footer>
        </aside>
    );
}

export default RightSidebar;
