import { useOutletContext } from 'react-router-dom';

import Stories from '../components/stories/Stories';
import PostCard from '../components/posts/PostCard';
import LoadingSpinner from '../components/common/LoadingSpinner';

function Dashboard() {
    const {
        user,
        posts,
        loading,
        error,
        fetchPosts,
        suggestions,
        onCreatePost,
    } = useOutletContext();

    return (
        <>
            <div className="welcome-heading">
                <div>
                    <span className="welcome-label">
                        YOUR COMMUNITY
                    </span>

                    <h1>
                        Good morning,{' '}
                        {user?.firstName || user?.username}
                        {' '}<span>✦</span>
                    </h1>

                    <p>
                        Here's what's happening in your world today.
                    </p>
                </div>

                <button
                    className="create-button"
                    onClick={onCreatePost}
                >
                    + Create post
                </button>
            </div>

            <Stories
                users={suggestions}
                user={user}
            />

            <div className="feed-heading">
                <h2>Your feed</h2>

                <button
                    className="filter-button"
                    onClick={fetchPosts}
                    disabled={loading}
                >
                    {loading ? 'Loading...' : 'Refresh ↻'}
                </button>
            </div>

            <div className="posts-list">
                {loading && (
                    <LoadingSpinner
                        message="Loading your community..."
                    />
                )}

                {!loading && error && (
                    <div className="feed-error">
                        <p>{error}</p>

                        <button onClick={fetchPosts}>
                            Try again
                        </button>
                    </div>
                )}

                {!loading && !error && posts.length === 0 && (
                    <div className="empty-feed">
                        <div className="community-icon">
                            ✦
                        </div>

                        <h3>Your feed is waiting for you</h3>

                        <p>
                            There are no posts yet. Share your first
                            moment with your community!
                        </p>

                        <button
                            className="create-button"
                            onClick={onCreatePost}
                        >
                            + Create your first post
                        </button>
                    </div>
                )}

                {!loading && !error && posts.map((post) => (
                    <PostCard
                        key={post.id}
                        post={post}
                        user={user}
                        onPostUpdated={fetchPosts}
                        onPostDeleted={fetchPosts}
                    />
                ))}
            </div>
        </>
    );
}

export default Dashboard;