import { useState } from 'react';

import Avatar from '../common/Avatar';

import { Link } from 'react-router-dom';

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

            {/* Current user */}
            <Link
                to={`/profile/${user?.username}`}
                className="current-user"
                style={{
                    textDecoration: 'none',
                    color: 'inherit',
                    cursor: 'pointer',
                }}
            >
                <Avatar
                    initials={`${user?.firstName?.[0] || ''}${user?.lastName?.[0] || ''}`.toUpperCase() || 'U'}
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

            {/* Suggestions heading */}
            <div className="suggestions-heading">
                <span>Suggested for you</span>

                <Link to="/explore" className="text-button">
                    See all
                </Link>
            </div>

            {/* Suggested users */}
            <div className="suggestions-list">
                {suggestions.map((suggestedUser) => (
                    <div
                        className="suggestion"
                        key={suggestedUser.username}
                    >
                        <Link
                            to={`/profile/${suggestedUser.username}`}
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
                                    `${suggestedUser.firstName?.[0] || ''}${suggestedUser.lastName?.[0] || ''}`.toUpperCase() ||
                                    'U'
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
                    <p>No suggestions available.</p>
                )}
            </div>

            {/* Community card */}
            <div className="community-card">
                <div className="community-icon">✦</div>

                <h3>Good things grow together.</h3>

                <p>
                    Share your moments, find your people and make
                    new connections.
                </p>
            </div>

            {/* Footer */}
            <footer className="right-footer">
                <div style={{
                    display: 'flex',
                    flexWrap: 'wrap',
                    gap: '5px 10px',
                }}>
                    <Link to="/about">About</Link>
                    <Link to="/help">Help</Link>
                    <Link to="/privacy">Privacy</Link>
                    <Link to="/terms">Terms</Link>
                </div>

                <div style={{ marginTop: '10px' }}>
                    Made with ♡
                    <br />
                    CONNECTLY © 2026
                </div>
            </footer>

        </aside>
    );
}

export default RightSidebar;
