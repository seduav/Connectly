import {
    useCallback,
    useEffect,
    useState,
} from 'react';

import postService from '../services/postService';

import { getErrorMessage } from '../utils/errorUtils';

export default function usePosts() {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const fetchPosts = useCallback(async () => {
        try {
            setLoading(true);
            setError('');

            const response = await postService.getAllPosts();

            setPosts(
                Array.isArray(response) ? response : []
            );
        } catch (err) {
            setError(
                getErrorMessage(
                    err,
                    'Unable to load posts. Please try again.'
                )
            );
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchPosts();
    }, [fetchPosts]);

    return {
        posts,
        loading,
        error,
        fetchPosts,
    };
}