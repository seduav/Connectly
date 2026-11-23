import Avatar from '../common/Avatar';

function StoryItem({ story }) {
    return (
        <button className="story">
            <div
                className={`story-ring ${
                    story.own ? 'own-story' : ''
                }`}
            >
                <Avatar
                    initials={story.initials}
                    color="lavender"
                    size="story"
                />

                {story.own && (
                    <span className="add-story">+</span>
                )}
            </div>

            <span>{story.name}</span>
        </button>
    );
}

export default StoryItem;