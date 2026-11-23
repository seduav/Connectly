import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Auth.css';

function Register() {
    const navigate = useNavigate();
    const { register } = useAuth();

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

        if (formData.password.length < 8 ||
            formData.password.length > 72) {
            setError('Password must contain between 8 and 72 characters.');
            return;
        }

        try {
            setLoading(true);

            const response = await register(formData);

            setSuccess(
                response.message || 'Account created successfully!'
            );

            setTimeout(() => navigate('/login'), 1500);
        } catch (err) {
            setError(
                err.response?.data?.message ||
                'Unable to create your account. Please try again.'
            );
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
                        <div className="auth-icon">✿</div>

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
                                maxLength={50}
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
                                maxLength={50}
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
                                minLength={3}
                                maxLength={30}
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
                                maxLength={100}
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
                                minLength={8}
                                maxLength={72}
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
                        <Link to="/login">Sign in</Link>
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
