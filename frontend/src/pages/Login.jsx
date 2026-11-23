import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Auth.css';

function Login() {
    const navigate = useNavigate();
    const { login } = useAuth();

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

            navigate('/', { replace: true });
        } catch (err) {
            const status = err.response?.status;

            if (status === 401 || status === 403) {
                setError('Invalid email or password.');
            } else {
                setError(
                    err.response?.data?.message ||
                    err.message ||
                    'Unable to login. Please try again.'
                );
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="auth-page">
            <div className="auth-decoration decoration-one" />
            <div className="auth-decoration decoration-two" />

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
                            <label htmlFor="email">Email address</label>

                            <input
                                id="email"
                                type="email"
                                name="email"
                                placeholder="you@example.com"
                                value={formData.email}
                                onChange={handleChange}
                                maxLength={100}
                                autoComplete="email"
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="password">Password</label>

                            <div className="password-input">
                                <input
                                    id="password"
                                    type={showPassword ? 'text' : 'password'}
                                    name="password"
                                    placeholder="Enter your password"
                                    value={formData.password}
                                    onChange={handleChange}
                                    maxLength={72}
                                    autoComplete="current-password"
                                    required
                                />

                                <button
                                    type="button"
                                    className="password-toggle"
                                    onClick={() =>
                                        setShowPassword((current) => !current)
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
                            <div className="auth-error" role="alert">
                                {error}
                            </div>
                        )}

                        <button
                            type="submit"
                            className="auth-submit"
                            disabled={loading}
                        >
                            {loading ? 'Signing in...' : 'Sign in'}
                        </button>
                    </form>

                    <div className="auth-divider">
                        <span>OR</span>
                    </div>

                    <p className="auth-switch">
                        Don't have an account?{' '}
                        <Link to="/register">Create account</Link>
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
