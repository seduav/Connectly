import {
    useCallback,
    useEffect,
    useState,
} from 'react';

import { AuthContext } from './AuthContext';

import authService from '../services/authService';

import { STORAGE_KEYS } from '../constants/storageConstants';
import { ERROR_MESSAGES } from '../constants/messages';

function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    const logout = useCallback(() => {
        authService.logout();

        setUser(null);
        setIsAuthenticated(false);
    }, []);

    const loadUser = useCallback(async () => {
        const token = localStorage.getItem(
            STORAGE_KEYS.AUTH_TOKEN
        );

        if (!token) {
            setUser(null);
            setIsAuthenticated(false);
            return;
        }

        try {
            const currentUser = await authService.getCurrentUser();

            setUser(currentUser);
            setIsAuthenticated(true);
        } catch (error) {
            console.error(
                'Unable to load authenticated user:',
                error
            );

            logout();
        }
    }, [logout]);

    useEffect(() => {
        async function initializeAuth() {
            try {
                await loadUser();
            } finally {
                setLoading(false);
            }
        }

        initializeAuth();
    }, [loadUser]);

    useEffect(() => {
        function handleUnauthorized() {
            logout();
        }

        window.addEventListener(
            'connectly:unauthorized',
            handleUnauthorized
        );

        return () => {
            window.removeEventListener(
                'connectly:unauthorized',
                handleUnauthorized
            );
        };
    }, [logout]);

    const login = async (credentials) => {
        await authService.login(credentials);
        await loadUser();

        if (!localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN)) {
            throw new Error(
                ERROR_MESSAGES.AUTHENTICATION_FAILED
            );
        }
    };

    const register = async (userData) => {
        return authService.register(userData);
    };

    const updateProfile = async (profileData) => {
        const updatedProfile = await authService.updateProfile(
            profileData
        );

        setUser((currentUser) => ({
            ...currentUser,
            firstName: updatedProfile.firstName,
            lastName: updatedProfile.lastName,
            bio: updatedProfile.bio,
            profilePictureUrl: updatedProfile.profilePictureUrl,
            dateOfBirth: updatedProfile.dateOfBirth,
        }));

        return updatedProfile;
    };

    const value = {
        user,
        loading,
        isAuthenticated,
        login,
        register,
        logout,
        updateProfile,
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

export { AuthProvider };