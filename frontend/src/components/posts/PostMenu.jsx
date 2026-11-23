function PostMenu({
                      show,
                      onToggle,
                      onEdit,
                      onDelete,
                      loading,
                  }) {
    return (
        <div className="post-menu-container">
            <button
                type="button"
                className="more-button"
                aria-label="More post options"
                aria-expanded={show}
                onClick={onToggle}
            >
                ···
            </button>

            {show && (
                <div className="post-dropdown-menu">
                    <button
                        type="button"
                        onClick={onEdit}
                        disabled={loading}
                    >
                        <span>✎</span>
                        Edit post
                    </button>

                    <button
                        type="button"
                        className="delete-post-option"
                        onClick={onDelete}
                        disabled={loading}
                    >
                        <span>⌫</span>
                        Delete post
                    </button>
                </div>
            )}
        </div>
    );
}

export default PostMenu;