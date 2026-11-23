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
        <div className="post-content post-body">
            {editing ? (
                <PostEditForm
                    content={editContent}
                    setContent={setEditContent}
                    loading={loading}
                    onSave={onSave}
                    onCancel={onCancel}
                />
            ) : (
                <p>{post.content}</p>
            )}

            {error && (
                <p
                    className="interaction-error"
                    role="alert"
                >
                    {error}
                </p>
            )}
        </div>
    );
}

export default PostContent;