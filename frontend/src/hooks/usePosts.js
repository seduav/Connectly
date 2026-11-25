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
        setLoading(true);
        setError('');

        try {
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
        let ignore = false;

        async function loadPosts() {
            try {
                const response = await postService.getAllPosts();

                if (!ignore) {
                    setPosts(
                        Array.isArray(response) ? response : []
                    );
                    setError('');
                }
            } catch (err) {
                if (!ignore) {
                    setError(
                        getErrorMessage(
                            err,
                            'Unable to load posts. Please try again.'
                        )
                    );
                }
            } finally {
                if (!ignore) {
                    setLoading(false);
                }
            }
        }

        loadPosts();

        return () => {
            ignore = true;
        };
    }, []);

    return {
        posts,
        loading,
        error,
        fetchPosts,
    };
}