function CommentItem({
                         comment,
                         isAuthor,
                         canDelete,
                         editing,
                         editingContent,
                         setEditingContent,
                         onStartEdit,
                         onSaveEdit,
                         onCancelEdit,
                         onDelete,
                     }) {
    return (
        <div className="comment-item">
            <div className="comment-details">
                <strong className="comment-author">
                    {comment.authorUsername}
                </strong>

                {editing ? (
                    <input
                        className="comment-edit-input"
                        value={editingContent}
                        onChange={(event) =>
                            setEditingContent(event.target.value)
                        }
                    />
                ) : (
                    <span className="comment-text">
                        {comment.content}
                    </span>
                )}
            </div>

            {(isAuthor || canDelete) && (
                <div className="comment-controls">
                    {isAuthor && (
                        editing ? (
                            <>
                                <button
                                    onClick={onSaveEdit}
                                    disabled={!editingContent.trim()}
                                >
                                    Save
                                </button>

                                <button onClick={onCancelEdit}>
                                    Cancel
                                </button>
                            </>
                        ) : (
                            <button onClick={onStartEdit}>
                                Edit
                            </button>
                        )
                    )}

                    {canDelete && (
                        <button onClick={onDelete}>
                            Delete
                        </button>
                    )}
                </div>
            )}
        </div>
    );
}

export default CommentItem;