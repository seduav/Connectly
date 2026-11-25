import { useLocation, useNavigate } from 'react-router-dom';

import { ROUTES } from '../../constants/routeConstants';

function MobileNavigation({ onCreatePost }) {
    const navigate = useNavigate();
    const location = useLocation();

    return (
        <nav className="mobile-navigation">
            <button
                aria-label="Home"
                className={
                    location.pathname === ROUTES.HOME ? 'active' : ''
                }
                onClick={() => navigate(ROUTES.HOME)}
            >
                ⌂
            </button>

            <button
                aria-label="Explore"
                onClick={() => navigate(ROUTES.EXPLORE)}
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
                onClick={() => navigate(ROUTES.NOTIFICATIONS)}
            >
                ♡
            </button>

            <button
                aria-label="Profile"
                onClick={() => navigate(ROUTES.PROFILE)}
            >
                ◉
            </button>
        </nav>
    );
}

export default MobileNavigation;
