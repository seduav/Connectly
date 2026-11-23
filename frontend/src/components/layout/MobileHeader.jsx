function MobileHeader({ darkMode, setDarkMode }) {
    return (
        <header className="mobile-header">
            <div className="brand">
                <div className="brand-symbol">c.</div>
                <span>connectly</span>
            </div>

            <button
                className="mobile-theme-button"
                onClick={() => setDarkMode(!darkMode)}
                aria-label="Toggle theme"
            >
                {darkMode ? '☀' : '☾'}
            </button>
        </header>
    );
}

export default MobileHeader;