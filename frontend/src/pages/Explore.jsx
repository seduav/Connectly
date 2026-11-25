import { useEffect } from 'react';
import { useOutletContext, Link } from 'react-router-dom';

import Avatar from '../components/common/Avatar';
import { getInitials } from '../utils/userUtils';

function Explore() {
    const { suggestions } = useOutletContext();

    useEffect(() => {
        document.title = 'Explore | Connectly';
    }, []);

    return (
        <section className="page-section">
            <div className="welcome-heading">
                <div>
                    <span className="welcome-label">
                        DISCOVER
                    </span>

                    <h1>Explore Connectly ✦</h1>

                    <p>
                        Discover people and new communities.
                    </p>
                </div>
            </div>

            <div className="suggestions-list">
                {suggestions.map((person) => (
                    <Link
                        to={`/profile/${person.username}`}
                        className="suggestion"
                        key={person.username}
                    >
                        <Avatar
                            initials={getInitials(
                                person.firstName,
                                person.lastName
                            )}
                            color="lavender"
                        />

                        <div className="suggestion-info">
                            <strong>{person.username}</strong>

                            <span>
                                {person.firstName} {person.lastName}
                            </span>
                        </div>
                    </Link>
                ))}
            </div>
        </section>
    );
}

export default Explore;