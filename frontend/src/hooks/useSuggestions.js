import { useCallback, useEffect, useState } from 'react';

import userService from '../services/userService';

export default function useSuggestions() {
    const [suggestions, setSuggestions] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchSuggestions = useCallback(async () => {
        try {
            setLoading(true);

            const response = await userService.getSuggestions();

            setSuggestions(
                Array.isArray(response) ? response : []
            );
        } catch (error) {
            console.error(
                'Unable to load suggestions:',
                error
            );
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchSuggestions();
    }, [fetchSuggestions]);

    return {
        suggestions,
        loading,
        fetchSuggestions,
    };
}