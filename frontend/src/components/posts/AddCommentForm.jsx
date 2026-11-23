function AddCommentForm({
                            content,
                            setContent,
                            loading,
                            onSubmit,
                        }) {
    return (
        <form
            className="add-comment"
            onSubmit={onSubmit}
        >
            <input
                type="text"
                placeholder="Add a comment..."
                value={content}
                onChange={(event) =>
                    setContent(event.target.value)
                }
                maxLength={2000}
            />

            <button
                type="submit"
                disabled={
                    loading || !content.trim()
                }
            >
                {loading ? 'Posting...' : 'Post'}
            </button>
        </form>
    );
}

export default AddCommentForm;