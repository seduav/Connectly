import { useState } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';

import { useAuth } from '../../hooks/useAuth';
import { useTheme } from '../../hooks/useTheme';

import Sidebar from './Sidebar';
import RightSidebar from './RightSidebar';
import MobileHeader from './MobileHeader';
import MobileNavigation from './MobileNavigation';

import CreatePostModal from '../modals/CreatePostModal';

import usePosts from '../../hooks/usePosts';
import useSuggestions from '../../hooks/useSuggestions';

import { ROUTES } from '../../constants/routeConstants';
import { CONFIRM_MESSAGES } from '../../constants/messages';

function DashboardLayout() {
    const { user, logout } = useAuth();
    const { darkMode, setDarkMode } = useTheme();
    const navigate = useNavigate();

    const [showCreatePost, setShowCreatePost] = useState(false);

    const {
        posts,
        loading,
        error,
        fetchPosts,
    } = usePosts();

    const { suggestions } = useSuggestions();

    function handleLogout() {
        if (window.confirm(CONFIRM_MESSAGES.LOGOUT)) {
            logout();
        }
    }

    async function handlePostCreated() {
        await fetchPosts();
        setShowCreatePost(false);
        navigate(ROUTES.HOME);
    }

    return (
        <div className={`app-shell ${darkMode ? 'dark-theme' : 'light-theme'}`}>
            <Sidebar
                user={user}
                darkMode={darkMode}
                setDarkMode={setDarkMode}
                onLogout={handleLogout}
                onCreatePost={() => setShowCreatePost(true)}
            />

            <main className="main-content">
                <MobileHeader
                    darkMode={darkMode}
                    setDarkMode={setDarkMode}
                />

                <div className="content-layout">
                    <div className="feed-column">
                        <Outlet
                            context={{
                                user,
                                posts,
                                loading,
                                error,
                                fetchPosts,
                                suggestions,
                                darkMode,
                                setDarkMode,
                                onCreatePost: () => setShowCreatePost(true),
                            }}
                        />
                    </div>

                    <RightSidebar
                        user={user}
                        suggestions={suggestions}
                    />
                </div>
            </main>

            <MobileNavigation
                onCreatePost={() => setShowCreatePost(true)}
            />

            {showCreatePost && (
                <CreatePostModal
                    onClose={() => setShowCreatePost(false)}
                    onPostCreated={handlePostCreated}
                />
            )}
        </div>
    );
}

export default DashboardLayout;