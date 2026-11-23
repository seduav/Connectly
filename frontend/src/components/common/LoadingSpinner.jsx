function LoadingSpinner({ message }) {
    return (
        <div className="empty-feed">
            <div className="loading-spinner" />
            {message && <p>{message}</p>}
        </div>
    );
}

export default LoadingSpinner;