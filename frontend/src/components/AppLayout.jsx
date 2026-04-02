import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { clearToken, getUser, isRecruiter, isUserRole } from "../utils/auth";

function SidebarLink({ to, children, end = false }) {
  return (
    <NavLink to={to} end={end} className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}>
      {children}
    </NavLink>
  );
}

export default function AppLayout() {
  const navigate = useNavigate();
  const user = getUser();

  function handleLogout() {
    clearToken();
    navigate("/login");
  }

  return (
    <div className="linkedin-app">
      <header className="linkedin-navbar navbar navbar-expand-lg bg-white border-bottom sticky-top">
        <div className="navbar-inner container-xxl d-flex flex-wrap align-items-center gap-3">
          <div className="navbar-brand">
            <div className="linkedin-logo">in</div>
            <div>
              <strong>LinkedIn Lite</strong>
              <p>Jobs, hiring and professional identity</p>
            </div>
          </div>

          <nav className="navbar-links nav flex-wrap gap-2">
            <NavLink to="/dashboard" end className={({ isActive }) => `nav-link ${isActive ? "active fw-semibold" : ""}`}>
              Home
            </NavLink>
            <NavLink to="/network" className={({ isActive }) => `nav-link ${isActive ? "active fw-semibold" : ""}`}>
              My Network
            </NavLink>
            <NavLink to="/search" className={({ isActive }) => `nav-link ${isActive ? "active fw-semibold" : ""}`}>
              Search
            </NavLink>
            <NavLink to="/companies" className={({ isActive }) => `nav-link ${isActive ? "active fw-semibold" : ""}`}>
              Companies
            </NavLink>
            <NavLink to="/messaging" className={({ isActive }) => `nav-link ${isActive ? "active fw-semibold" : ""}`}>
              Messaging
            </NavLink>
            <NavLink to="/notifications" className={({ isActive }) => `nav-link ${isActive ? "active fw-semibold" : ""}`}>
              Notifications
            </NavLink>
            <NavLink to="/profile" className={({ isActive }) => `nav-link ${isActive ? "active fw-semibold" : ""}`}>
              Profile
            </NavLink>
            <NavLink to="/jobs" className={({ isActive }) => `nav-link ${isActive ? "active fw-semibold" : ""}`}>
              Jobs
            </NavLink>
            {isUserRole() && (
              <NavLink
                to="/applications"
                className={({ isActive }) => `nav-link ${isActive ? "active fw-semibold" : ""}`}
              >
                Applications
              </NavLink>
            )}
            {isRecruiter() && (
              <NavLink
                to="/recruiter/jobs"
                className={({ isActive }) => `nav-link ${isActive ? "active fw-semibold" : ""}`}
              >
                Recruiter
              </NavLink>
            )}
          </nav>

          <div className="navbar-profile d-flex align-items-center gap-2">
            <div className="profile-mini-avatar">
              {(user?.name || "U").slice(0, 1).toUpperCase()}
            </div>
            <div className="navbar-profile-copy">
              <strong>{user?.name || "Professional"}</strong>
              <span>{user?.role || "USER"}</span>
            </div>
            <button type="button" className="btn btn-outline-secondary btn-sm" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </div>
      </header>

      <main className="linkedin-shell container-xxl py-4">
        <div className="row g-4">
          <aside className="left-sidebar col-12 col-lg-3">
            <section className="sidebar-card profile-card">
              <div className="profile-cover" />
              <div className="profile-avatar large">
                {(user?.name || "U").slice(0, 1).toUpperCase()}
              </div>
              <h2>{user?.name || "Professional"}</h2>
              <p className="profile-role-label">{user?.headline || user?.role || "USER"}</p>
              <p className="sidebar-text">{user?.bio || "Add your headline and about section to stand out to recruiters and peers."}</p>
              <div className="sidebar-pills">
                <span className="info-pill">{user?.skills || "Add skills"}</span>
              </div>
            </section>

            <section className="sidebar-card sidebar-nav-card">
              <p className="sidebar-heading">Workspace</p>
              <div className="sidebar-links">
                <SidebarLink to="/dashboard" end>Dashboard</SidebarLink>
                <SidebarLink to="/network">My Network</SidebarLink>
                <SidebarLink to="/search">Search</SidebarLink>
                <SidebarLink to="/companies">Companies</SidebarLink>
                <SidebarLink to="/messaging">Messaging</SidebarLink>
                <SidebarLink to="/notifications">Notifications</SidebarLink>
                <SidebarLink to="/profile">Profile</SidebarLink>
                <SidebarLink to="/jobs">Jobs</SidebarLink>
                {isUserRole() && <SidebarLink to="/applications">My Applications</SidebarLink>}
                {isRecruiter() && <SidebarLink to="/recruiter/jobs">Manage Jobs</SidebarLink>}
              </div>
            </section>
          </aside>

          <section className="content-column col-12 col-lg-9">
            <Outlet />
          </section>
        </div>
      </main>
    </div>
  );
}
