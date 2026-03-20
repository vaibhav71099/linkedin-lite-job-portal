import { useEffect, useState } from "react";
import api from "../api";

export default function AppliedJobsPage() {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");

  useEffect(() => {
    async function loadApplications() {
      try {
        const response = await api.get("/api/applications/my");
        setApplications(response.data.data);
      } catch (error) {
        setMessage(error.response?.data?.message || "Unable to load applied jobs.");
      } finally {
        setLoading(false);
      }
    }

    loadApplications();
  }, []);

  return (
    <section className="page-section">
      <div className="feed-card job-hero">
        <div className="job-hero-copy">
          <p className="eyebrow">Applications</p>
          <h2>Jobs you have already applied to</h2>
          <p className="support-text">
            Track submitted applications and keep a clean record of your ongoing job search.
          </p>
        </div>
      </div>

      <div className="job-grid">
        {loading && <div className="feed-card empty-card">Loading applications...</div>}
        {!loading && applications.length === 0 && (
          <div className="feed-card empty-card">
            <strong>No applications found</strong>
            <p className="support-text">Start exploring jobs and apply to interesting roles.</p>
          </div>
        )}

        {applications.map((application) => (
          <article key={application.id} className="feed-card job-card">
            <div className="job-card-header">
              <div>
                <p className="company-tag">{application.company}</p>
                <h3>{application.jobTitle}</h3>
              </div>
              <span className="location-pill">{application.location}</span>
            </div>

            <p className="job-description">Applied on {application.appliedDate}</p>

            <div className="job-card-footer">
              <div className="job-chip-group">
                <span className="job-chip">Application submitted</span>
              </div>
            </div>
          </article>
        ))}
      </div>

      {message && <p className="message error">{message}</p>}
    </section>
  );
}
