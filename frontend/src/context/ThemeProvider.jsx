import { useEffect, useState } from 'react';

import { ThemeContext } from './ThemeContext';

import { STORAGE_KEYS } from '../constants/storageConstants';
import { THEMES } from '../constants/appConstants';

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