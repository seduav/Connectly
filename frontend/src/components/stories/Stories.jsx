import StoryItem from './StoryItem';

import { getInitials } from '../../utils/userUtils';

function Stories({ users = [], user }) {
    const stories = [
        {
            id: 'own',
            name: 'Your story',
            initials: getInitials(
                user?.firstName,
                user?.lastName
            ),
            own: true,
        },

        ...users.map((person) => ({
            id: person.id,
            name: person.username,
            initials: getInitials(
                person.firstName,
                person.lastName
            ),
        })),
    ];

    return (
        <section className="stories-section">
            <div className="section-heading">
                <div>
                    <h2>Stories</h2>
                    <p>See what your friends are sharing</p>
                </div>
            </div>

            <div className="stories-list">
                {stories.map((story) => (
                    <StoryItem
                        key={story.id}
                        story={story}
                    />
                ))}
            </div>
        </section>
    );
}

export default Stories;