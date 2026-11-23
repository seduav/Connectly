function PostEditForm({
                          content,
                          setContent,
                          loading,
                          onSave,
                          onCancel,
                      }) {
    return (
        <div className="post-edit-form">
            <textarea
                value={content}
                onChange={(event) =>
                    setContent(event.target.value)
                }
                maxLength={5000}
                autoFocus
                disabled={loading}
                placeholder="Edit your post..."
            />

            <div className="post-edit-footer">
                <span>
                    {content.length} / 5000
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
                        disabled={
                            loading ||
                            !content.trim()
                        }
                    >
                        {loading ? 'Saving...' : 'Save changes'}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default PostEditForm;