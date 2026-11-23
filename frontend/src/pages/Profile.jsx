import { useOutletContext, useParams } from 'react-router-dom';

import Avatar from '../components/common/Avatar';
import PostCard from '../components/posts/PostCard';

import { getInitials } from '../utils/userUtils';

function Profile() {
    const {
        user,
        posts,
        fetchPosts,
        suggestions = [],
        users = [],
    } = useOutletContext();

    const { username } = useParams();

    // Find the profile being viewed
    const profileUser =
        username === user?.username
            ? user
            : users.find((person) => person.username === username) ||
            suggestions.find((person) => person.username === username);

    // Only show posts belonging to this user
    const userPosts = posts.filter(
        (post) => post.authorUsername === username
    );

    if (!profileUser) {
        return (
            <section className="page-section">
                <div className="empty-feed">
                    <h3>User not found</h3>
                    <p>This profile doesn't exist.</p>
                </div>
            </section>
        );
    }

    return (
        <section className="page-section">
            <div className="welcome-heading">
                <div>
                    <span className="welcome-label">
                        {username === user?.username
                            ? 'YOUR SPACE'
                            : 'COMMUNITY MEMBER'}
                    </span>

                    <h1>
                        {username === user?.username
                            ? 'My Profile'
                            : `${profileUser.firstName}'s Profile`}
                    </h1>
                </div>
            </div>

            <div className="profile-card">
                <div className="sidebar-profile">
                    <Avatar
                        initials={getInitials(
                            profileUser.firstName,
                            profileUser.lastName
                        )}
                        color="lavender"
                        size="large"
                    />

                    <div>
                        <strong>
                            {profileUser.firstName} {profileUser.lastName}
                        </strong>

                        <span>@{profileUser.username}</span>
                    </div>
                </div>

                <div className="profile-stats">
                    <div>
                        <span>Posts: </span>
                        <strong>{userPosts.length}</strong>
                    </div>
                </div>
            </div>

            <br />

            <div className="posts-list">
                {userPosts.length === 0 ? (
                    <div className="empty-feed">
                        <h3>No posts yet</h3>
                    </div>
                ) : (
                    userPosts.map((post) => (
                        <PostCard
                            key={post.id}
                            post={post}
                            user={user}
                            onPostUpdated={fetchPosts}
                            onPostDeleted={fetchPosts}
                        />
                    ))
                )}
            </div>
        </section>
    );
}

export default Profile;
