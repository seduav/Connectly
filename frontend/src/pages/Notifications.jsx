function Notifications() {
    return (
        <section className="page-section">
            <div className="welcome-heading">
                <div>
                    <span className="welcome-label">
                        YOUR ACTIVITY
                    </span>

                    <h1>Notifications ♡</h1>

                    <p>
                        Stay updated with what's happening
                        in your community.
                    </p>
                </div>
            </div>

            <div className="empty-feed">
                <div className="community-icon">♡</div>

                <h3>You're all caught up!</h3>

                <p>
                    New notifications will appear here.
                </p>
            </div>
        </section>
    );
}

export default Notifications;