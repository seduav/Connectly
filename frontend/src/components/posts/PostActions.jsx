function PostActions({
                         liked,
                         likeLoading,
                         onToggleLike,
                         onToggleComments,
                         commentsOpen,
                     }) {
    return (
        <div className="post-actions post-actions-updated">

            <button
                type="button"
                className={`post-action-item ${liked ? 'liked' : ''}`}
                onClick={onToggleLike}
                disabled={likeLoading}
            >
                <span className="post-action-icon">
                    {liked ? '♥' : '♡'}
                </span>
                <span>{liked ? 'Liked' : 'Like'}</span>
            </button>

            <button
                type="button"
                className="post-action-item"
                onClick={onToggleComments}
            >
                <span className="post-action-icon">▢</span>
                <span>
                    {commentsOpen ? 'Hide comments' : 'Comment'}
                </span>
            </button>

            <button
                type="button"
                className="post-action-item"
                onClick={() => {
                    if (navigator.share) {
                        navigator.share({
                            title: 'Connectly post',
                            text: 'Check out this post on Connectly!',
                            url: window.location.href,
                        }).catch(error => {
                            if (error.name !== 'AbortError') {
                                console.error('Share failed:', error);
                            }
                        });
                    } else if (navigator.clipboard) {
                        navigator.clipboard.writeText(
                            window.location.href
                        );
                    }
                }}
            >
                <span className="post-action-icon">↗</span>
                <span>Share</span>
            </button>

        </div>
    );
}

export default PostActions;
