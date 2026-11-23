import { useState, useEffect } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';

import { useAuth } from '../../context/AuthContext';

import Sidebar from './Sidebar';
import RightSidebar from './RightSidebar';
import MobileHeader from './MobileHeader';
import MobileNavigation from './MobileNavigation';

import CreatePostModal from '../modals/CreatePostModal';

import usePosts from '../../hooks/usePosts';
import useSuggestions from '../../hooks/useSuggestions';

function DashboardLayout() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    // Restore saved theme from localStorage.
    const [darkMode, setDarkMode] = useState(() => {
        return localStorage.getItem('connectly-theme') === 'dark';
    });

    const [showCreatePost, setShowCreatePost] = useState(false);

    // Persist theme whenever it changes.
    useEffect(() => {
        localStorage.setItem(
            'connectly-theme',
            darkMode ? 'dark' : 'light'
        );
    }, [darkMode]);

    const {
        posts,
        loading,
        error,
        fetchPosts,
    } = usePosts();

    const {
        suggestions,
    } = useSuggestions();

    function handleLogout() {
        if (
            window.confirm('Are you sure you want to log out?')
        ) {
            logout();
        }
    }

    async function handlePostCreated() {
        await fetchPosts();
        setShowCreatePost(false);
        navigate('/');
    }

    return (
        <div
            className={`app-shell ${
                darkMode ? 'dark-theme' : 'light-theme'
            }`}
        >
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

                                // Theme settings
                                darkMode,
                                setDarkMode,

                                onCreatePost: () =>
                                    setShowCreatePost(true),
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
