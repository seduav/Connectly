import { useEffect } from 'react';

function Messages() {
    useEffect(() => {
        document.title = 'Messages | Connectly';
    }, []);

    return (
        <section className="page-section">
            <div className="welcome-heading">
                <div>
                    <span className="welcome-label">
                        STAY CONNECTED
                    </span>

                    <h1>Your messages ✉</h1>

                    <p>
                        Connect and have conversations with friends.
                    </p>
                </div>
            </div>

            <div className="empty-feed">
                <div className="community-icon">✉</div>

                <h3>No conversations yet</h3>

                <p>
                    Your conversations will appear here.
                </p>
            </div>
        </section>
    );
}

export default Messages;