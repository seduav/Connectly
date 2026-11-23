import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function ProtectedRoute({ allowedRoles }) {
    const { user, loading, isAuthenticated } = useAuth();

    if (loading) {
        return (
            <div className="route-loading">
                <div className="loading-spinner" />
                <p>Loading Connectly...</p>
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    if (
        allowedRoles &&
        !allowedRoles.includes(user?.role)
    ) {
        return <Navigate to="/" replace />;
    }

    return <Outlet />;
}

export default ProtectedRoute;
