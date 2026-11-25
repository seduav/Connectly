import PostEditForm from './PostEditForm';

function PostContent({
                         post,
                         editing,
                         editContent,
                         setEditContent,
                         loading,
                         error,
                         onSave,
                         onCancel,
                     }) {
    return (
        <div className="post-content post-content-updated">

            {editing ? (
                <div className="post-edit-form">
                    <textarea
                        value={editContent}
                        onChange={event =>
                            setEditContent(event.target.value)
                        }
                        placeholder="What's on your mind?"
                        disabled={loading}
                    />

                    {error && (
                        <p className="interaction-error">{error}</p>
                    )}

                    <div className="post-edit-footer">
                        <span>
                            {editContent.length} characters
                        </span>

                        <div className="post-edit-actions">
                            <button
                                type="button"
                                className="cancel-edit-button"
                                onClick={onCancel}
                                disabled={loading}
                            >
                                Cancel
                            </button>

                            <button
                                type="button"
                                className="save-post-button"
                                onClick={onSave}
                                disabled={loading || !editContent.trim()}
                            >
                                {loading ? 'Saving...' : 'Save changes'}
                            </button>
                        </div>
                    </div>
                </div>
            ) : (
                <>
                    {post.content && (
                        <div className="post-body">
                            <p>{post.content}</p>
                        </div>
                    )}

                    {post.imageUrl && (
                        <div className="post-image-container">
                            <img
                                src={post.imageUrl}
                                alt="Post"
                                className="post-image"
                            />
                        </div>
                    )}

                    {error && (
                        <p className="interaction-error">{error}</p>
                    )}
                </>
            )}
        </div>
    );
}

export default PostContent;