import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api";
import { getUser, saveUser } from "../utils/auth";

export default function DashboardPage() {
  const [profile, setProfile] = useState(getUser());
  const [applications, setApplications] = useState([]);
  const [myJobs, setMyJobs] = useState([]);
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
              <span>{profile?.role === "RECRUITER" ? "Active postings" : "Applications sent"}</span>
              <strong>{profile?.role === "RECRUITER" ? myJobs.length : applications.length}</strong>
            </div>
            <div className="metric-tile">
              <span>Account status</span>
              <strong>{profile?.emailVerified ? "Verified" : "Pending"}</strong>
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
            <p className="profile-line">{profile?.email || "No email available"}</p>
            <p className="profile-line">{profile?.bio || "Add a bio to strengthen your professional presence."}</p>
            <div className="profile-meta-row">
              <span className="soft-pill">Skills: {profile?.skills || "Add your skills"}</span>
            </div>
          </div>
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

          {loading && <p className="support-text">Loading dashboard...</p>}

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

          {message && <p className="message error">{message}</p>}
        </section>
      </div>

      <aside className="right-rail">
        <section className="sidebar-card">
          <p className="sidebar-heading">Quick stats</p>
          <div className="mini-stat-grid">
            <div className="mini-stat">
              <span>Network profile</span>
              <strong>{profile?.bio ? "Strong" : "Needs bio"}</strong>
            </div>
            <div className="mini-stat">
              <span>{profile?.role === "RECRUITER" ? "Listings" : "Applications"}</span>
              <strong>{profile?.role === "RECRUITER" ? myJobs.length : applications.length}</strong>
            </div>
          </div>
        </section>

        <section className="sidebar-card">
          <p className="sidebar-heading">Suggested next step</p>
          <p className="sidebar-text">
            {profile?.role === "RECRUITER"
              ? "Post a focused role with a clear company and location to attract better applicants."
              : "Complete your bio and skills so recruiters can scan your profile faster."}
          </p>
        </section>
      </aside>
    </section>
  );
}
