import { Navigate, Outlet } from 'react-router-dom';

import { useAuth } from '../hooks/useAuth';

import { ROUTES } from '../constants/routeConstants';
import { UI_MESSAGES } from '../constants/messages';

function ProtectedRoute({ allowedRoles }) {
    const { user, loading, isAuthenticated } = useAuth();

    if (loading) {
        return (
            <div className="route-loading">
                <div className="loading-spinner" />
                <p>{UI_MESSAGES.LOADING}</p>
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to={ROUTES.LOGIN} replace />;
    }

    if (
        allowedRoles &&
        !allowedRoles.includes(user?.role)
    ) {
        return <Navigate to={ROUTES.HOME} replace />;
    }

    return <Outlet />;
}

export default ProtectedRoute;
