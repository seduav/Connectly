import { useCallback, useEffect, useState } from 'react';

import likeService from '../services/likeService';

export default function useLikes(postId) {
    const [liked, setLiked] = useState(false);
    const [likesCount, setLikesCount] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        let active = true;

        async function loadLikes() {
            try {
                const [countResponse, likedResponse] =
                    await Promise.all([
                        likeService.getLikesCount(postId),
                        likeService.hasUserLikedPost(postId),
                    ]);

                if (!active) return;

                setLikesCount(countResponse.likesCount ?? 0);
                setLiked(likedResponse.liked ?? false);
            } catch (err) {
                console.error('Unable to load likes:', err);

                if (active) {
                    setError('Unable to load likes.');
                }
            }
        }

        loadLikes();

        return () => {
            active = false;
        };
    }, [postId]);

    const toggleLike = useCallback(async () => {
        if (loading) return;

        try {
            setLoading(true);
            setError('');

            const response = await likeService.toggleLike(postId);

            setLiked(response.liked);
            setLikesCount(response.likesCount);
        } catch (err) {
            console.error('Unable to update like:', err);
            setError('Unable to update like.');
        } finally {
            setLoading(false);
        }
    }, [postId, loading]);

    return {
        liked,
        likesCount,
        loading,
        error,
        toggleLike,
    };
}