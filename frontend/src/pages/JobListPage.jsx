import { useEffect, useState } from "react";
import api from "../api";
import { getUser, isRecruiter } from "../utils/auth";

export default function JobListPage() {
  const [jobs, setJobs] = useState([]);
  const [pagination, setPagination] = useState({
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0
  });
  const [searchTerm, setSearchTerm] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [applyingId, setApplyingId] = useState(null);
  const currentUser = getUser();

  useEffect(() => {
    async function fetchJobs() {
      try {
        const response = await api.get("/jobs");
        const pagedData = response.data.data;
        setJobs(Array.isArray(pagedData?.content) ? pagedData.content : []);
        setPagination({
          page: pagedData?.page ?? 0,
          size: pagedData?.size ?? 10,
          totalElements: pagedData?.totalElements ?? 0,
          totalPages: pagedData?.totalPages ?? 0
        });
      } catch (error) {
        setJobs([]);
        setMessage(error.response?.data?.message || "Unable to load jobs.");
      } finally {
        setLoading(false);
      }
    }

    fetchJobs();
  }, []);

  const filteredJobs = jobs.filter((job) => {
    const normalizedSearch = searchTerm.toLowerCase();
    return (
      job.title.toLowerCase().includes(normalizedSearch) ||
      job.company.toLowerCase().includes(normalizedSearch) ||
      job.location.toLowerCase().includes(normalizedSearch)
    );
  });

  async function handleApply(jobId) {
    setApplyingId(jobId);
    setMessage("");

    try {
      const response = await api.post("/api/applications", { jobId });
      setMessage(response.data.message || "Application submitted successfully.");
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to apply for this job.");
    } finally {
      setApplyingId(null);
    }
  }

  return (
    <section className="page-section">
      <div className="feed-card job-hero">
        <div className="job-hero-copy">
          <p className="eyebrow">Find your next role</p>
          <h2>Discover jobs from recruiters on your network-style dashboard</h2>
          <p className="support-text">
            Search by title, company, or location, then move from browsing to application in one flow.
          </p>
          <div className="job-hero-stats">
            <div className="mini-stat">
              <span>Open jobs</span>
              <strong>{pagination.totalElements}</strong>
            </div>
            <div className="mini-stat">
              <span>Signed in as</span>
              <strong>{currentUser?.role || "USER"}</strong>
            </div>
            <div className="mini-stat">
              <span>Page</span>
              <strong>{pagination.page + 1}</strong>
            </div>
          </div>
        </div>

        <div className="job-search">
          <label>
            <span>Search jobs</span>
            <input
              type="text"
              value={searchTerm}
              placeholder="Search title, company, or location"
              onChange={(event) => setSearchTerm(event.target.value)}
            />
          </label>
        </div>
      </div>

      {message && (
        <p className={`message ${message.includes("successfully") ? "success" : "error"}`}>
          {message}
        </p>
      )}

      <div className="job-grid">
        {loading && <div className="feed-card empty-card">Loading jobs...</div>}
        {!loading && filteredJobs.length === 0 && (
          <div className="feed-card empty-card">
            <strong>No jobs matched your search</strong>
            <p className="support-text">Try a different keyword or location.</p>
          </div>
        )}

        {filteredJobs.map((job) => (
          <article key={job.id} className="feed-card job-card">
            <div className="job-card-header">
              <div>
                <p className="company-tag">{job.company}</p>
                <h3>{job.title}</h3>
              </div>
              <span className="location-pill">{job.location}</span>
            </div>

            <p className="job-description">{job.description}</p>

            <div className="job-card-footer">
              <div className="job-chip-group">
                <span className="job-chip">Recruiter: {job.recruiterName || "Team"}</span>
                <span className="job-chip">{job.location}</span>
              </div>

              {!isRecruiter() && (
                <button
                  type="button"
                  className="primary-button"
                  onClick={() => handleApply(job.id)}
                  disabled={applyingId === job.id}
                >
                  {applyingId === job.id ? "Applying..." : "Apply Job"}
                </button>
              )}
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
