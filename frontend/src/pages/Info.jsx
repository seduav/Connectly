import { useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';

const pageContent = {
    '/about': {
        title: 'About Connectly',
        subtitle: 'A place to connect, share and grow together.',
        description: `
            Connectly is a social networking application designed
            to bring people closer together.

            Share your moments, discover new people, interact with
            your community and build meaningful connections.

            Our goal is to make connecting with others simple,
            enjoyable and accessible to everyone.
        `,
    },

    '/help': {
        title: 'Help Center',
        subtitle: 'How can we help you?',
        description: `
            Welcome to the Connectly Help Center.

            If you need assistance, here are some things you can
            explore:

            • Account registration and login
            • Updating your profile
            • Creating and managing posts
            • Liking and commenting on posts
            • Connecting with other users
            • Managing your account settings

            More help resources and support options will be
            available here soon.
        `,
    },

    '/privacy': {
        title: 'Privacy Policy',
        subtitle: 'Your privacy matters to us.',
        description: `
            Connectly values your privacy and is committed to
            protecting your personal information.

            We aim to handle your account information responsibly
            and provide you with control over your profile.

            This page will contain information about the data
            we collect, how it is used, and your privacy rights.

            Our complete privacy policy will be available here soon.
        `,
    },

    '/terms': {
        title: 'Terms of Service',
        subtitle: 'The guidelines for using Connectly.',
        description: `
            By using Connectly, you agree to use the platform
            responsibly and respectfully.

            Users are expected to:

            • Respect other members of the community.
            • Avoid sharing harmful or inappropriate content.
            • Keep their account information secure.
            • Avoid impersonating other users.
            • Use the platform in accordance with applicable laws.

            Our complete terms of service will be available here soon.
        `,
    },
};

function Info() {
    const location = useLocation();
    const page = pageContent[location.pathname];

    useEffect(() => {
        document.title = `${page?.title || 'Connectly'} | Connectly`;
    }, [page]);

    if (!page) {
        return null;
    }

    return (
        <div className="feed-column">
            <div className="welcome-heading">
                <div>
                    <span className="welcome-label">
                        CONNECTLY
                    </span>

                    <h1>{page.title}</h1>

                    <p>{page.subtitle}</p>
                </div>
            </div>

            <div className="stories-section">
                <div className="section-heading">
                    <h2>{page.title}</h2>
                </div>

                <p
                    style={{
                        whiteSpace: 'pre-line',
                        fontSize: '13px',
                        lineHeight: '2',
                        color: 'var(--text-secondary)',
                    }}
                >
                    {page.description}
                </p>

                <div style={{ marginTop: '25px' }}>
                    <Link
                        to="/"
                        className="text-button"
                        style={{ textDecoration: 'none' }}
                    >
                        ← Back to Home
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default Info;
