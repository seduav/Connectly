import { useState } from 'react';

import postService from '../../services/postService';

import useLikes from '../../hooks/useLikes';
import useComments from '../../hooks/useComments';

import { getErrorMessage } from '../../utils/errorUtils';

import PostHeader from './PostHeader';
import PostContent from './PostContent';
import PostActions from './PostActions';
import CommentsSection from './CommentsSection';

function PostCard({
                      post,
                      user,
                      onPostUpdated,
                      onPostDeleted,
                  }) {
    // Like management.
    const {
        liked,
        likesCount,
        loading: likeLoading,
        toggleLike,
    } = useLikes(post.id);

    // Comment management.
    const {
        comments,
        commentsCount,
        loading: commentsLoading,
        submitting: submittingComment,
        error: commentError,
        loadComments,
        createComment,
        updateComment,
        deleteComment,
    } = useComments(post.id);

    // Post UI state.
    const [showPostMenu, setShowPostMenu] = useState(false);
    const [editingPost, setEditingPost] = useState(false);
    const [postEditContent, setPostEditContent] = useState(post.content);
    const [postLoading, setPostLoading] = useState(false);
    const [postError, setPostError] = useState('');

    // Comment UI state.
    const [commentsOpen, setCommentsOpen] = useState(false);
    const [commentContent, setCommentContent] = useState('');

    const [editingCommentId, setEditingCommentId] = useState(null);
    const [editingContent, setEditingContent] = useState('');

    const isPostAuthor =
        post.authorUsername === user?.username;

    // Toggle comments.
    async function handleToggleComments() {
        if (!commentsOpen) {
            setCommentsOpen(true);
            await loadComments();
        } else {
            setCommentsOpen(false);
        }
    }

    // Create comment.
    async function handleCreateComment(event) {
        event.preventDefault();

        const content = commentContent.trim();

        if (!content) return;

        const result = await createComment(content);

        if (result) {
            setCommentContent('');
        }
    }

    // Edit comment.
    function handleStartEditComment(comment) {
        setEditingCommentId(comment.id);
        setEditingContent(comment.content);
    }

    async function handleUpdateComment(commentId) {
        const content = editingContent.trim();

        if (!content) return;

        const result = await updateComment(
            commentId,
            content
        );

        if (result) {
            setEditingCommentId(null);
            setEditingContent('');
        }
    }

    function handleCancelEditComment() {
        setEditingCommentId(null);
        setEditingContent('');
    }

    // Delete comment.
    async function handleDeleteComment(commentId) {
        if (!window.confirm('Delete this comment?')) {
            return;
        }

        await deleteComment(commentId);
    }

    // Comment permissions.
    function isCommentAuthor(comment) {
        return comment.authorUsername === user?.username;
    }

    function canDeleteComment(comment) {
        return (
            isCommentAuthor(comment) ||
            isPostAuthor
        );
    }

    // Start editing post.
    function handleStartEditPost() {
        setPostEditContent(post.content);
        setEditingPost(true);
        setShowPostMenu(false);
        setPostError('');
    }

    // Save edited post.
    async function handleUpdatePost() {
        const content = postEditContent.trim();

        if (!content || postLoading) return;

        try {
            setPostLoading(true);
            setPostError('');

            await postService.updatePost(
                post.id,
                content
            );

            setEditingPost(false);
            setShowPostMenu(false);

            await onPostUpdated();
        } catch (error) {
            console.error('Unable to update post:', error);

            setPostError(
                getErrorMessage(
                    error,
                    'Unable to update your post.'
                )
            );
        } finally {
            setPostLoading(false);
        }
    }

    // Delete post.
    async function handleDeletePost() {
        const confirmed = window.confirm(
            'Are you sure you want to delete this post? This action cannot be undone.'
        );

        if (!confirmed || postLoading) return;

        try {
            setPostLoading(true);
            setPostError('');

            await postService.deletePost(post.id);

            await onPostDeleted();
        } catch (error) {
            console.error('Unable to delete post:', error);

            setPostError(
                getErrorMessage(
                    error,
                    'Unable to delete your post.'
                )
            );
        } finally {
            setPostLoading(false);
        }
    }

    // Cancel post editing.
    function handleCancelEditPost() {
        setEditingPost(false);
        setPostEditContent(post.content);
        setPostError('');
    }

    return (
        <article
            className={`post-card ${
                isPostAuthor ? 'own-post' : ''
            }`}
        >

            <PostHeader
                post={post}
                isPostAuthor={isPostAuthor}
                showPostMenu={showPostMenu}
                onToggleMenu={() =>
                    setShowPostMenu((current) => !current)
                }
                onEdit={handleStartEditPost}
                onDelete={handleDeletePost}
                postLoading={postLoading}
            />

            <PostContent
                post={post}
                editing={editingPost}
                editContent={postEditContent}
                setEditContent={setPostEditContent}
                loading={postLoading}
                error={postError}
                onSave={handleUpdatePost}
                onCancel={handleCancelEditPost}
            />

            <PostActions
                liked={liked}
                likesCount={likesCount}
                commentsCount={commentsCount}
                likeLoading={likeLoading}
                commentsOpen={commentsOpen}
                onToggleLike={toggleLike}
                onToggleComments={handleToggleComments}
            />

            {commentsOpen && (
                <div className="post-content post-engagement">
                    <CommentsSection
                        comments={comments}
                        loading={commentsLoading}
                        error={commentError}
                        commentContent={commentContent}
                        setCommentContent={setCommentContent}
                        submittingComment={submittingComment}
                        onCreateComment={handleCreateComment}
                        editingCommentId={editingCommentId}
                        editingContent={editingContent}
                        setEditingContent={setEditingContent}
                        onStartEdit={handleStartEditComment}
                        onSaveEdit={handleUpdateComment}
                        onCancelEdit={handleCancelEditComment}
                        onDelete={handleDeleteComment}
                        isCommentAuthor={isCommentAuthor}
                        canDeleteComment={canDeleteComment}
                    />
                </div>
            )}

        </article>
    );
}

export default PostCard;