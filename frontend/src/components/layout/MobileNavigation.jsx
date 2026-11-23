import { useLocation, useNavigate } from 'react-router-dom';

function MobileNavigation({ onCreatePost }) {
    const navigate = useNavigate();
    const location = useLocation();

    return (
        <nav className="mobile-navigation">
            <button
                aria-label="Home"
                className={location.pathname === '/' ? 'active' : ''}
                onClick={() => navigate('/')}
            >
                ⌂
            </button>

            <button
                aria-label="Explore"
                onClick={() => navigate('/explore')}
            >
                ⌕
            </button>

            <button
                aria-label="Create post"
                onClick={onCreatePost}
            >
                ＋
            </button>

            <button
                aria-label="Notifications"
                onClick={() => navigate('/notifications')}
            >
                ♡
            </button>

            <button
                aria-label="Profile"
                onClick={() => navigate('/profile')}
            >
                ◉
            </button>
        </nav>
    );
}

export default MobileNavigation;