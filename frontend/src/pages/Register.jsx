import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import { useAuth } from '../hooks/useAuth';
import { useTheme } from '../hooks/useTheme';

import { ROUTES } from '../constants/routeConstants';
import { VALIDATION } from '../constants/validationConstants';
import {
    ERROR_MESSAGES,
    SUCCESS_MESSAGES,
} from '../constants/messages';

import './Auth.css';

function Register() {
    const navigate = useNavigate();
    const { register } = useAuth();
    const { darkMode, setDarkMode } = useTheme();

    useEffect(() => {
        document.title = 'Register | Connectly';
    }, []);

    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        firstName: '',
        lastName: '',
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

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
        setSuccess('');

        if (
            formData.password.length < VALIDATION.PASSWORD.MIN_LENGTH ||
            formData.password.length > VALIDATION.PASSWORD.MAX_LENGTH
        ) {
            setError(
                `Password must contain between ${VALIDATION.PASSWORD.MIN_LENGTH} and ${VALIDATION.PASSWORD.MAX_LENGTH} characters.`
            );
            return;
        }

        try {
            setLoading(true);

            const response = await register(formData);

            setSuccess(
                response.message || SUCCESS_MESSAGES.ACCOUNT_CREATED
            );

            setTimeout(() => navigate(ROUTES.LOGIN), 1500);
        } catch (err) {
            setError(
                err.response?.data?.message ||
                ERROR_MESSAGES.REGISTRATION_FAILED
            );
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className={`auth-page ${darkMode ? 'dark-theme' : 'light-theme'}`}>
            <div className="auth-decoration decoration-one" />
            <div className="auth-decoration decoration-two" />

            <button
                type="button"
                className="auth-theme-toggle"
                onClick={() => setDarkMode((current) => !current)}
                aria-label={darkMode ? 'Switch to light theme' : 'Switch to dark theme'}
                title={darkMode ? 'Switch to light theme' : 'Switch to dark theme'}
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
                        <h1>Join Connectly!</h1>
                        <p>
                            Your community is waiting.
                            Create an account and start connecting.
                        </p>
                    </div>

                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="firstName">First name</label>
                            <input
                                id="firstName"
                                name="firstName"
                                value={formData.firstName}
                                onChange={handleChange}
                                maxLength={VALIDATION.NAME.MAX_LENGTH}
                                autoComplete="given-name"
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="lastName">Last name</label>
                            <input
                                id="lastName"
                                name="lastName"
                                value={formData.lastName}
                                onChange={handleChange}
                                maxLength={VALIDATION.NAME.MAX_LENGTH}
                                autoComplete="family-name"
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="username">Username</label>
                            <input
                                id="username"
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                minLength={VALIDATION.USERNAME.MIN_LENGTH}
                                maxLength={VALIDATION.USERNAME.MAX_LENGTH}
                                autoComplete="username"
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="registerEmail">Email address</label>
                            <input
                                id="registerEmail"
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                maxLength={VALIDATION.EMAIL.MAX_LENGTH}
                                autoComplete="email"
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="registerPassword">Password</label>
                            <input
                                id="registerPassword"
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                minLength={VALIDATION.PASSWORD.MIN_LENGTH}
                                maxLength={VALIDATION.PASSWORD.MAX_LENGTH}
                                autoComplete="new-password"
                                required
                            />
                        </div>

                        {error && (
                            <div className="auth-error" role="alert">
                                {error}
                            </div>
                        )}

                        {success && (
                            <div className="auth-success" role="status">
                                {success}
                            </div>
                        )}

                        <button
                            type="submit"
                            className="auth-submit"
                            disabled={loading || Boolean(success)}
                        >
                            {loading ? 'Creating account...' : 'Create account'}
                        </button>
                    </form>

                    <div className="auth-divider">
                        <span>OR</span>
                    </div>

                    <p className="auth-switch">
                        Already have an account?{' '}
                        <Link to={ROUTES.LOGIN}>Sign in</Link>
                    </p>
                </div>

                <p className="auth-footer">
                    Made with ♡ for meaningful connections.
                </p>
            </div>
        </div>
    );
}

export default Register;