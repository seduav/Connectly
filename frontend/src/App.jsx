import {
    BrowserRouter,
    Navigate,
    Route,
    Routes,
} from 'react-router-dom';

import { useAuth } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';

import ProtectedRoute from './routes/ProtectedRoute';

import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Explore from './pages/Explore';
import Notifications from './pages/Notifications';
import Messages from './pages/Messages';
import Profile from './pages/Profile';
import Settings from './pages/Settings';
import Info from './pages/Info';

import DashboardLayout from './components/layout/DashboardLayout';

import './App.css';

function App() {
    const { isAuthenticated, loading } = useAuth();

    if (loading) {
        return (
            <div className="route-loading">
                <div className="loading-spinner" />
                Loading Connectly...
            </div>
        );
    }

    return (
        <BrowserRouter>
            <ThemeProvider>
                <Routes>
                    <Route
                        path="/login"
                        element={
                            isAuthenticated
                                ? <Navigate to="/" replace />
                                : <Login />
                        }
                    />

                    <Route
                        path="/register"
                        element={
                            isAuthenticated
                                ? <Navigate to="/" replace />
                                : <Register />
                        }
                    />

                    <Route element={<ProtectedRoute />}>
                        <Route element={<DashboardLayout />}>
                            <Route
                                index
                                element={<Dashboard />}
                            />

                            <Route
                                path="explore"
                                element={<Explore />}
                            />

                            <Route
                                path="notifications"
                                element={<Notifications />}
                            />

                            <Route
                                path="messages"
                                element={<Messages />}
                            />

                            <Route
                                path="profile"
                                element={<Profile />}
                            />

                            <Route
                                path="profile/:username"
                                element={<Profile />}
                            />

                            <Route
                                path="settings"
                                element={<Settings />}
                            />

                            <Route
                                path="about"
                                element={<Info />}
                            />

                            <Route
                                path="help"
                                element={<Info />}
                            />

                            <Route
                                path="privacy"
                                element={<Info />}
                            />

                            <Route
                                path="terms"
                                element={<Info />}
                            />
                        </Route>
                    </Route>

                    <Route
                        path="*"
                        element={<Navigate to="/" replace />}
                    />
                </Routes>
            </ThemeProvider>
        </BrowserRouter>
    );
}

export default App;
