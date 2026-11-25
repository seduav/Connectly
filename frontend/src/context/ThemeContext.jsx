import { createContext, useContext, useEffect, useState } from 'react';

import { STORAGE_KEYS } from '../constants/storageConstants';
import { THEMES } from '../constants/appConstants';

const ThemeContext = createContext(null);

export function ThemeProvider({ children }) {
    const [darkMode, setDarkMode] = useState(() => {
        return localStorage.getItem(STORAGE_KEYS.THEME) === THEMES.DARK;
    });

    useEffect(() => {
        localStorage.setItem(
            STORAGE_KEYS.THEME,
            darkMode ? THEMES.DARK : THEMES.LIGHT
        );
    }, [darkMode]);

    return (
        <ThemeContext.Provider value={{ darkMode, setDarkMode }}>
            {children}
        </ThemeContext.Provider>
    );
}

export function useTheme() {
    const context = useContext(ThemeContext);

    if (!context) {
        throw new Error('useTheme must be used within ThemeProvider');
    }

    return context;
}
