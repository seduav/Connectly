import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';

import { ROUTES } from '../constants/routeConstants';
import { VALIDATION } from '../constants/validationConstants';
import { ERROR_MESSAGES } from '../constants/messages';

import './Auth.css';

function Login() {
    const navigate = useNavigate();
    const { login } = useAuth();
    const { darkMode, setDarkMode } = useTheme();

    useEffect(() => {
        document.title = 'Login | Connectly';
    }, []);

    const [formData, setFormData] = useState({
        email: '',
        password: '',
    });

    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    function handleChange(event) {
        const { name, value } = event.target;

        setFormData((current) => ({
            ...current,
            [name]: value,
        }));
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError('');

        if (!formData.email.trim() || !formData.password) {
            setError('Please enter your email and password.');
            return;
        }

        try {
            setLoading(true);

            await login({
                email: formData.email.trim(),
                password: formData.password,
            });

            navigate(ROUTES.HOME, { replace: true });
        } catch (err) {
            const status = err.response?.status;

            if (status === 401 || status === 403) {
                setError(ERROR_MESSAGES.INVALID_CREDENTIALS);
            } else {
                setError(
                    err.response?.data?.message ||
                    err.message ||
                    ERROR_MESSAGES.LOGIN_FAILED
                );
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <div
            className={`auth-page ${
                darkMode ? 'dark-theme' : 'light-theme'
            }`}
        >
            <div className="auth-decoration decoration-one" />
            <div className="auth-decoration decoration-two" />

            <button
                type="button"
                className="auth-theme-toggle"
                onClick={() =>
                    setDarkMode((current) => !current)
                }
                aria-label={
                    darkMode
                        ? 'Switch to light theme'
                        : 'Switch to dark theme'
                }
                title={
                    darkMode
                        ? 'Switch to light theme'
                        : 'Switch to dark theme'
                }
            >
                {darkMode ? '☀️' : '🌙'}
            </button>

            <div className="auth-container">
                <div className="auth-brand">
                    <div className="brand-symbol">c.</div>
                    <span>connectly</span>
                </div>

                <div className="auth-card">
                    <div className="auth-heading">
                        <div className="auth-icon">✦</div>

                        <h1>Welcome back!</h1>

                        <p>
                            Your people are waiting.
                            Sign in to continue connecting.
                        </p>
                    </div>

                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="email">
                                Email address
                            </label>

                            <input
                                id="email"
                                type="email"
                                name="email"
                                placeholder="you@example.com"
                                value={formData.email}
                                onChange={handleChange}
                                maxLength={VALIDATION.EMAIL.MAX_LENGTH}
                                autoComplete="email"
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="password">
                                Password
                            </label>

                            <div className="password-input">
                                <input
                                    id="password"
                                    type={
                                        showPassword
                                            ? 'text'
                                            : 'password'
                                    }
                                    name="password"
                                    placeholder="Enter your password"
                                    value={formData.password}
                                    onChange={handleChange}
                                    maxLength={
                                        VALIDATION.PASSWORD.MAX_LENGTH
                                    }
                                    autoComplete="current-password"
                                    required
                                />

                                <button
                                    type="button"
                                    className="password-toggle"
                                    onClick={() =>
                                        setShowPassword(
                                            (current) => !current
                                        )
                                    }
                                    aria-label={
                                        showPassword
                                            ? 'Hide password'
                                            : 'Show password'
                                    }
                                >
                                    {showPassword ? 'Hide' : 'Show'}
                                </button>
                            </div>
                        </div>

                        {error && (
                            <div
                                className="auth-error"
                                role="alert"
                            >
                                {error}
                            </div>
                        )}

                        <button
                            type="submit"
                            className="auth-submit"
                            disabled={loading}
                        >
                            {loading
                                ? 'Signing in...'
                                : 'Sign in'}
                        </button>
                    </form>

                    <div className="auth-divider">
                        <span>OR</span>
                    </div>

                    <p className="auth-switch">
                        Don't have an account?{' '}
                        <Link to={ROUTES.REGISTER}>
                            Create account
                        </Link>
                    </p>
                </div>

                <p className="auth-footer">
                    Made with ♡ for meaningful connections.
                </p>
            </div>
        </div>
    );
}

export default Login;
