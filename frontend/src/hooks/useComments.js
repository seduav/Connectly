import {
    useCallback,
    useEffect,
    useRef,
    useState,
} from 'react';

import commentService from '../services/commentService';

import { getErrorMessage } from '../utils/errorUtils';

export default function useComments(postId) {
    const [comments, setComments] = useState([]);
    const [commentsCount, setCommentsCount] = useState(0);

    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');

    const submittingRef = useRef(false);

    // Load the initial comment count.
    useEffect(() => {
        let active = true;

        async function loadCommentCount() {
            try {
                const response =
                    await commentService.getCommentsByPost(postId);

                if (!active) return;

                const loadedComments = Array.isArray(response)
                    ? response
                    : [];

                setCommentsCount(loadedComments.length);
            } catch (err) {
                console.error(
                    'Unable to load comment count:',
                    err
                );
            }
        }

        loadCommentCount();

        return () => {
            active = false;
        };
    }, [postId]);

    // Fetch all comments.
    const loadComments = useCallback(async () => {
        try {
            setLoading(true);
            setError('');

            const response =
                await commentService.getCommentsByPost(postId);

            const loadedComments = Array.isArray(response)
                ? response
                : [];

            setComments(loadedComments);
            setCommentsCount(loadedComments.length);

            return loadedComments;
        } catch (err) {
            console.error('Unable to load comments:', err);

            setError(
                'Unable to load comments. Please try again.'
            );

            return null;
        } finally {
            setLoading(false);
        }
    }, [postId]);

    // Create a comment.
    const createComment = useCallback(async (content) => {
        if (!content.trim() || submittingRef.current) {
            return null;
        }

        try {
            submittingRef.current = true;
            setSubmitting(true);
            setError('');

            const createdComment =
                await commentService.createComment(
                    postId,
                    content.trim()
                );

            setComments((current) => [
                ...current,
                createdComment,
            ]);

            setCommentsCount((current) => current + 1);

            return createdComment;
        } catch (err) {
            console.error('Unable to create comment:', err);

            setError(
                getErrorMessage(
                    err,
                    'Unable to publish your comment.'
                )
            );

            return null;
        } finally {
            submittingRef.current = false;
            setSubmitting(false);
        }
    }, [postId]);

    // Update a comment.
    const updateComment = useCallback(
        async (commentId, content) => {
            if (!content.trim()) return null;

            try {
                setError('');

                const updatedComment =
                    await commentService.updateComment(
                        postId,
                        commentId,
                        content.trim()
                    );

                setComments((current) =>
                    current.map((comment) =>
                        comment.id === commentId
                            ? updatedComment
                            : comment
                    )
                );

                return updatedComment;
            } catch (err) {
                console.error('Unable to update comment:', err);

                setError('Unable to update comment.');

                return null;
            }
        },
        [postId]
    );

    // Delete a comment.
    const deleteComment = useCallback(
        async (commentId) => {
            try {
                setError('');

                await commentService.deleteComment(
                    postId,
                    commentId
                );

                setComments((current) =>
                    current.filter(
                        (comment) => comment.id !== commentId
                    )
                );

                setCommentsCount((current) =>
                    Math.max(0, current - 1)
                );

                return true;
            } catch (err) {
                console.error('Unable to delete comment:', err);

                setError('Unable to delete comment.');

                return false;
            }
        },
        [postId]
    );

    return {
        comments,
        commentsCount,
        loading,
        submitting,
        error,
        loadComments,
        createComment,
        updateComment,
        deleteComment,
    };
}