function PostActions({
                         liked,
                         likesCount,
                         commentsCount,
                         likeLoading,
                         commentsOpen,
                         onToggleLike,
                         onToggleComments,
                     }) {
    return (
        <>
            <div className="post-actions">
                <div className="post-action-group">
                    <button
                        type="button"
                        className={`action-button like-button ${
                            liked ? 'liked' : ''
                        }`}
                        onClick={onToggleLike}
                        disabled={likeLoading}
                        aria-label={
                            liked ? 'Unlike post' : 'Like post'
                        }
                    >
                        {liked ? '♥' : '♡'}
                    </button>

                    <button
                        type="button"
                        className="action-button"
                        onClick={onToggleComments}
                        aria-label="Comments"
                    >
                        🗪
                    </button>
                </div>
            </div>

            <div className="post-content post-engagement">
                <div className="engagement-counts">
                    <strong className="likes-count">
                        {likesCount}{' '}
                        {likesCount === 1 ? 'like' : 'likes'}
                    </strong>

                    <button
                        type="button"
                        className="comments-link"
                        onClick={onToggleComments}
                    >
                        {commentsCount}{' '}
                        {commentsCount === 1
                            ? 'comment'
                            : 'comments'}
                    </button>
                </div>

                <button
                    type="button"
                    className="comments-link"
                    onClick={onToggleComments}
                >
                    {commentsOpen
                        ? 'Hide comments'
                        : 'View comments'}
                </button>
            </div>
        </>
    );
}

export default PostActions;