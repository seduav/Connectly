import { useLocation, useNavigate } from 'react-router-dom';

import Avatar from '../common/Avatar';
import { getInitials } from '../../utils/userUtils';

function Sidebar({
                     user,
                     darkMode,
                     setDarkMode,
                     onCreatePost,
                     onLogout,
                 }) {
    const navigate = useNavigate();
    const location = useLocation();

    const navigation = [
        { label: 'Home', icon: '⌂', path: '/' },
        { label: 'Explore', icon: '⌕', path: '/explore' },
        {
            label: 'Notifications',
            icon: '♡',
            path: '/notifications',
        },
        { label: 'Messages', icon: '✉', path: '/messages' },
        { label: 'Profile', icon: '◉', path: `/profile/${user?.username}` },
        { label: 'Settings', icon: '⚙', path: '/settings' },
    ];

    return (
        <aside className="sidebar">
            <div className="brand">
                <div className="brand-symbol">c.</div>
                <span>connectly</span>
            </div>

            {/* Current user profile */}
            <div
                className="sidebar-profile"
                onClick={() => navigate(`/profile/${user?.username}`)}
                style={{ cursor: 'pointer' }}
            >
                <Avatar
                    initials={getInitials(
                        user?.firstName,
                        user?.lastName
                    )}
                    color="lavender"
                    size="large"
                />

                <div>
                    <strong>
                        {user?.firstName} {user?.lastName}
                    </strong>

                    <span>@{user?.username}</span>
                </div>
            </div>

            <nav className="navigation">
                <p className="nav-heading">MENU</p>

                {navigation.map((item) => (
                    <button
                        key={item.label}
                        className={`nav-item ${
                            location.pathname === item.path ||
                            (item.label === 'Profile' &&
                                location.pathname.startsWith('/profile'))
                                ? 'active'
                                : ''
                        }`}
                        onClick={() => navigate(item.path)}
                    >
                        <span className="nav-icon">
                            {item.icon}
                        </span>

                        <span>{item.label}</span>
                    </button>
                ))}

                <button
                    className="nav-item"
                    onClick={onCreatePost}
                >
                    <span className="nav-icon">+</span>
                    <span>Create post</span>
                </button>
            </nav>

            <div className="sidebar-bottom">
                <button
                    className="nav-item theme-button"
                    onClick={() => setDarkMode(!darkMode)}
                >
                    <span className="nav-icon">
                        {darkMode ? '☀' : '☾'}
                    </span>

                    <span>
                        {darkMode ? 'Light mode' : 'Dark mode'}
                    </span>
                </button>

                <button
                    className="nav-item logout-button"
                    onClick={onLogout}
                >
                    <span className="nav-icon">↪</span>
                    <span>Log out</span>
                </button>
            </div>
        </aside>
    );
}

export default Sidebar;
