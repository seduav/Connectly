export function formatPostDate(date) {
    if (!date) return '';

    const postDate = new Date(date);

    if (Number.isNaN(postDate.getTime())) return '';

    return postDate.toLocaleString(undefined, {
        dateStyle: 'medium',
        timeStyle: 'short',
    });
}

export function hasBeenEdited(createdAt, updatedAt) {
    if (!createdAt || !updatedAt) return false;

    const created = new Date(createdAt).getTime();
    const updated = new Date(updatedAt).getTime();

    if (
        Number.isNaN(created) ||
        Number.isNaN(updated)
    ) {
        return false;
    }

    return updated > created;
}