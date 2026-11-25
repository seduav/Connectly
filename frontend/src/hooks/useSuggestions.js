import { useCallback, useEffect, useState } from 'react';

import userService from '../services/userService';

export default function useSuggestions() {
    const [suggestions, setSuggestions] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchSuggestions = useCallback(async () => {
        setLoading(true);

        try {
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
        let ignore = false;

        async function loadSuggestions() {
            try {
                const response = await userService.getSuggestions();

                if (!ignore) {
                    setSuggestions(
                        Array.isArray(response) ? response : []
                    );
                }
            } catch (error) {
                if (!ignore) {
                    console.error(
                        'Unable to load suggestions:',
                        error
                    );
                }
            } finally {
                if (!ignore) {
                    setLoading(false);
                }
            }
        }

        loadSuggestions();

        return () => {
            ignore = true;
        };
    }, []);

    return {
        suggestions,
        loading,
        fetchSuggestions,
    };
}