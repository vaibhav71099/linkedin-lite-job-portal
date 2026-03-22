import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api";
import { getUser, saveUser } from "../utils/auth";

export default function DashboardPage() {
  const [profile, setProfile] = useState(getUser());
  const [applications, setApplications] = useState([]);
  const [myJobs, setMyJobs] = useState([]);
  const [connections, setConnections] = useState([]);
  const [invitations, setInvitations] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadDashboard() {
      try {
        const profileResponse = await api.get("/api/users/me");
        const nextProfile = profileResponse.data.data;
        setProfile(nextProfile);
        saveUser(nextProfile);

        if (nextProfile.role === "USER") {
          const applicationsResponse = await api.get("/api/applications/my");
          setApplications(applicationsResponse.data.data);
        }

        if (nextProfile.role === "RECRUITER") {
          const jobsResponse = await api.get("/jobs/mine");
          setMyJobs(jobsResponse.data.data);
        }

        const [connectionsResponse, invitationsResponse] = await Promise.all([
          api.get("/api/network/connections"),
          api.get("/api/network/invitations")
        ]);
        setConnections(connectionsResponse.data.data);
        setInvitations(invitationsResponse.data.data);
      } catch (error) {
        setMessage(error.response?.data?.message || "Unable to load dashboard.");
      } finally {
        setLoading(false);
      }
    }

    loadDashboard();
  }, []);

  return (
    <section className="page-section">
      <div className="content-feed">
        <section className="feed-card hero-card">
          <div className="hero-copy">
            <p className="eyebrow">Professional dashboard</p>
            <h2>
              {profile?.role === "RECRUITER"
                ? "Find talent and keep every hiring step visible"
                : "Track jobs, sharpen your profile, and apply with confidence"}
            </h2>
            <p className="support-text">
              {profile?.role === "RECRUITER"
                ? "Your recruiter workspace keeps job postings, applicants, and profile credibility in one place."
                : "Your home feed highlights profile strength, application activity, and open roles tailored to your journey."}
            </p>

            <div className="hero-actions">
              <Link to="/profile" className="primary-button inline-button">
                Update profile
              </Link>
              <Link
                to={profile?.role === "RECRUITER" ? "/recruiter/jobs" : "/jobs"}
                className="ghost-button inline-button"
              >
                {profile?.role === "RECRUITER" ? "Open recruiter tools" : "Explore jobs"}
              </Link>
            </div>
          </div>

          <div className="hero-metrics">
            <div className="metric-tile">
              <span>Role</span>
              <strong>{profile?.role || "USER"}</strong>
            </div>
            <div className="metric-tile">
              <span>Connections</span>
              <strong>{connections.length}</strong>
            </div>
            <div className="metric-tile">
              <span>Pending invites</span>
              <strong>{invitations.length}</strong>
            </div>
          </div>
        </section>

        <section className="feed-card profile-panel">
          <div className="profile-top">
            <div className="profile-avatar">
              {(profile?.name || "U").slice(0, 1).toUpperCase()}
            </div>
            <div>
              <p className="eyebrow">Profile Snapshot</p>
              <h3>{profile?.name || "Loading..."}</h3>
            </div>
          </div>

          <div className="profile-stack">
            <p className="profile-line">{profile?.headline || "Add a headline that tells people what you do."}</p>
            <p className="profile-line">
              {[profile?.currentCompany, profile?.location].filter(Boolean).join(" • ") || profile?.email || "No profile metadata yet"}
            </p>
            <p className="profile-line">{profile?.bio || "Add a bio to strengthen your professional presence."}</p>
            <div className="profile-meta-row">
              <span className="soft-pill">Skills: {profile?.skills || "Add your skills"}</span>
            </div>
          </div>
        </section>

        <section className="feed-card activity-panel">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Network momentum</p>
              <h3>{invitations.length > 0 ? "New invitations waiting for you" : "Build your circle intentionally"}</h3>
            </div>
            <Link to="/network" className="text-link">Open network</Link>
          </div>

          {loading && <p className="support-text">Loading dashboard...</p>}

          {!loading && invitations.length === 0 && connections.length === 0 && (
            <div className="empty-state">
              <strong>No network activity yet</strong>
              <p className="support-text">Complete your profile and start sending connection requests.</p>
            </div>
          )}

          <div className="application-list">
            {invitations.slice(0, 4).map((invitation) => (
              <article key={invitation.id} className="application-row">
                <div>
                  <strong>{invitation.requester.name}</strong>
                  <p>{invitation.requester.headline || "Professional invitation"}</p>
                </div>
                <span className="application-badge">Invitation</span>
              </article>
            ))}

            {invitations.length === 0 &&
              connections.slice(0, 4).map((connection) => (
                <article key={connection.id} className="application-row">
                  <div>
                    <strong>{connection.name}</strong>
                    <p>{connection.headline || "Connected professional"}</p>
                  </div>
                  <span className="application-badge">Connected</span>
                </article>
              ))}
          </div>

          {message && <p className="message error">{message}</p>}
        </section>

        <section className="feed-card activity-panel">
          <div className="section-heading">
            <div>
              <p className="eyebrow">{profile?.role === "RECRUITER" ? "Hiring" : "Applications"}</p>
              <h3>{profile?.role === "RECRUITER" ? "Your latest posted jobs" : "Recent applied jobs"}</h3>
            </div>
            <Link
              to={profile?.role === "RECRUITER" ? "/recruiter/jobs" : "/applications"}
              className="text-link"
            >
              View details
            </Link>
          </div>

          {!loading && profile?.role === "USER" && applications.length === 0 && (
            <div className="empty-state">
              <strong>No applications yet</strong>
              <p className="support-text">Search open roles and submit your first application.</p>
            </div>
          )}

          {!loading && profile?.role === "RECRUITER" && myJobs.length === 0 && (
            <div className="empty-state">
              <strong>No jobs posted yet</strong>
              <p className="support-text">Create your first posting to start receiving applications.</p>
            </div>
          )}

          <div className="application-list">
            {profile?.role === "USER" &&
              applications.slice(0, 4).map((application) => (
                <article key={application.id} className="application-row">
                  <div>
                    <strong>{application.jobTitle}</strong>
                    <p>
                      {application.company} • {application.location}
                    </p>
                  </div>
                  <span className="application-badge">Applied {application.appliedDate}</span>
                </article>
              ))}
            {profile?.role === "RECRUITER" &&
              myJobs.slice(0, 4).map((job) => (
                <article key={job.id} className="application-row">
                  <div>
                    <strong>{job.title}</strong>
                    <p>
                      {job.company} • {job.location}
                    </p>
                  </div>
                  <span className="application-badge">Live</span>
                </article>
              ))}
          </div>
        </section>
      </div>

      <aside className="right-rail">
        <section className="sidebar-card">
          <p className="sidebar-heading">Quick stats</p>
          <div className="mini-stat-grid">
            <div className="mini-stat">
              <span>Profile signal</span>
              <strong>{profile?.headline && profile?.bio && profile?.skills ? "Strong" : "Incomplete"}</strong>
            </div>
            <div className="mini-stat">
              <span>Connections</span>
              <strong>{connections.length}</strong>
            </div>
          </div>
        </section>

        <section className="sidebar-card">
          <p className="sidebar-heading">Suggested next step</p>
          <p className="sidebar-text">
            {profile?.headline && profile?.location
              ? "Open My Network and send focused invitations so your professional graph starts compounding."
              : "Add a headline and location so your professional identity reads clearly at first glance."}
          </p>
        </section>
      </aside>
    </section>
  );
}
