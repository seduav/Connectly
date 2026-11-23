import { useState } from 'react';

import postService from '../../services/postService';

import { getErrorMessage } from '../../utils/errorUtils';

function CreatePostModal({ onClose, onPostCreated }) {
    const [content, setContent] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    async function handleSubmit(event) {
        event.preventDefault();

        const trimmedContent = content.trim();

        if (!trimmedContent) {
            setError('Please write something before publishing.');
            return;
        }

        if (loading) return;

        try {
            setLoading(true);
            setError('');

            await postService.createPost(trimmedContent);

            await onPostCreated();

            onClose();
        } catch (err) {
            console.error('Unable to create post:', err);

            setError(
                getErrorMessage(
                    err,
                    'Unable to publish your post. Please try again.'
                )
            );
        } finally {
            setLoading(false);
        }
    }

    function handleOverlayClick(event) {
        if (
            event.target === event.currentTarget &&
            !loading
        ) {
            onClose();
        }
    }

    return (
        <div
            className="modal-overlay"
            onMouseDown={handleOverlayClick}
        >
            <div
                className="create-post-modal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="create-post-title"
            >
                <div className="modal-header">
                    <h2 id="create-post-title">
                        Create a post
                    </h2>

                    <button
                        type="button"
                        className="modal-close"
                        onClick={onClose}
                        disabled={loading}
                        aria-label="Close modal"
                    >
                        ×
                    </button>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="modal-user">
                        <div className="brand-symbol">
                            c.
                        </div>

                        <div>
                            <strong>
                                Share with your community
                            </strong>

                            <span>
                                Express yourself on Connectly
                            </span>
                        </div>
                    </div>

                    <textarea
                        className="create-post-textarea"
                        placeholder="What's on your mind?"
                        value={content}
                        onChange={(event) =>
                            setContent(event.target.value)
                        }
                        maxLength={5000}
                        autoFocus
                        disabled={loading}
                    />

                    <div className="character-counter">
                        {content.length} / 5000 characters
                    </div>

                    {error && (
                        <div
                            className="modal-error"
                            role="alert"
                        >
                            {error}
                        </div>
                    )}

                    <button
                        type="submit"
                        className="modal-submit"
                        disabled={
                            loading || !content.trim()
                        }
                    >
                        {loading
                            ? 'Publishing...'
                            : 'Share post'}
                    </button>
                </form>
            </div>
        </div>
    );
}

export default CreatePostModal;