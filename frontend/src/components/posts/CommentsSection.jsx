import CommentItem from './CommentItem';
import AddCommentForm from './AddCommentForm';

function CommentsSection({
                             comments,
                             loading,
                             error,
                             commentContent,
                             setCommentContent,
                             submittingComment,
                             onCreateComment,
                             editingCommentId,
                             editingContent,
                             setEditingContent,
                             onStartEdit,
                             onSaveEdit,
                             onCancelEdit,
                             onDelete,
                             isCommentAuthor,
                             canDeleteComment,
                         }) {
    return (
        <div className="comments-section">
            {loading && <p>Loading comments...</p>}

            {error && (
                <p className="feed-error">{error}</p>
            )}

            {!loading && comments.length === 0 && (
                <p>
                    No comments yet. Start the conversation!
                </p>
            )}

            {comments.map((comment) => (
                <CommentItem
                    key={comment.id}
                    comment={comment}
                    isAuthor={isCommentAuthor(comment)}
                    canDelete={canDeleteComment(comment)}
                    editing={editingCommentId === comment.id}
                    editingContent={editingContent}
                    setEditingContent={setEditingContent}
                    onStartEdit={() => onStartEdit(comment)}
                    onSaveEdit={() => onSaveEdit(comment.id)}
                    onCancelEdit={onCancelEdit}
                    onDelete={() => onDelete(comment.id)}
                />
            ))}

            <AddCommentForm
                content={commentContent}
                setContent={setCommentContent}
                loading={submittingComment}
                onSubmit={onCreateComment}
            />
        </div>
    );
}

export default CommentsSection;