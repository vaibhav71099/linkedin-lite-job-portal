import { Navigate, Route, Routes } from "react-router-dom";
import DashboardPage from "./pages/DashboardPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import JobListPage from "./pages/JobListPage";
import ProfilePage from "./pages/ProfilePage";
import NetworkPage from "./pages/NetworkPage";
import MessagingPage from "./pages/MessagingPage";
import NotificationsPage from "./pages/NotificationsPage";
import CompaniesPage from "./pages/CompaniesPage";
import SearchPage from "./pages/SearchPage";
import AppliedJobsPage from "./pages/AppliedJobsPage";
import RecruiterJobsPage from "./pages/RecruiterJobsPage";
import AppLayout from "./components/AppLayout";
import { getToken, isRecruiter, isUserRole } from "./utils/auth";

function ProtectedRoute({ children }) {
  return getToken() ? children : <Navigate to="/login" replace />;
}

function PublicOnlyRoute({ children }) {
  return getToken() ? <Navigate to="/dashboard" replace /> : children;
}

function RoleRoute({ children, allowRecruiter = false, allowUser = false }) {
  if (!getToken()) {
    return <Navigate to="/login" replace />;
  }

  if ((allowRecruiter && isRecruiter()) || (allowUser && isUserRole())) {
    return children;
  }

  return <Navigate to="/dashboard" replace />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="/login" element={<PublicOnlyRoute><LoginPage /></PublicOnlyRoute>} />
      <Route path="/register" element={<PublicOnlyRoute><RegisterPage /></PublicOnlyRoute>} />

      <Route
        element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/network" element={<NetworkPage />} />
        <Route path="/messaging" element={<MessagingPage />} />
        <Route path="/notifications" element={<NotificationsPage />} />
        <Route path="/companies" element={<CompaniesPage />} />
        <Route path="/search" element={<SearchPage />} />
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/jobs" element={<JobListPage />} />
        <Route
          path="/applications"
          element={
            <RoleRoute allowUser>
              <AppliedJobsPage />
            </RoleRoute>
          }
        />
        <Route
          path="/recruiter/jobs"
          element={
            <RoleRoute allowRecruiter>
              <RecruiterJobsPage />
            </RoleRoute>
          }
        />
      </Route>
    </Routes>
  );
}
