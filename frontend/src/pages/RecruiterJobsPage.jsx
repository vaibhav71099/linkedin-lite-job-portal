import { useEffect, useState } from "react";
import api from "../api";

const initialForm = {
  title: "",
  description: "",
  company: "",
  location: ""
};

export default function RecruiterJobsPage() {
  const [formData, setFormData] = useState(initialForm);
  const [jobs, setJobs] = useState([]);
  const [selectedJobId, setSelectedJobId] = useState(null);
  const [applicants, setApplicants] = useState([]);
  const [message, setMessage] = useState("");
  const [saving, setSaving] = useState(false);
  const [loadingJobs, setLoadingJobs] = useState(true);
  const [loadingApplicants, setLoadingApplicants] = useState(false);

  async function loadJobs() {
    setLoadingJobs(true);
    try {
      const response = await api.get("/jobs/mine");
      setJobs(response.data.data);
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to load recruiter jobs.");
    } finally {
      setLoadingJobs(false);
    }
  }

  useEffect(() => {
    loadJobs();
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();
    setSaving(true);
    setMessage("");

    try {
      const response = await api.post("/jobs", formData);
      setMessage(response.data.message || "Job posted successfully.");
      setFormData(initialForm);
      await loadJobs();
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to post job.");
    } finally {
      setSaving(false);
    }
  }

  async function handleViewApplicants(jobId) {
    setSelectedJobId(jobId);
    setLoadingApplicants(true);
    setMessage("");

    try {
      const response = await api.get(`/api/applications/job/${jobId}`);
      setApplicants(response.data.data);
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to load applicants.");
    } finally {
      setLoadingApplicants(false);
    }
  }

  return (
    <section className="recruiter-layout">
      <section className="feed-card recruiter-form">
        <p className="eyebrow">Recruiter Workspace</p>
        <h2>Create a new job post</h2>
        <p className="support-text">
          Publish roles, keep them organized, and review applicants from a single workflow.
        </p>

        <form className="form-grid" onSubmit={handleSubmit}>
          <label>
            <span>Job Title</span>
            <input
              type="text"
              value={formData.title}
              onChange={(event) => setFormData({ ...formData, title: event.target.value })}
              required
            />
          </label>

          <label>
            <span>Description</span>
            <textarea
              rows="5"
              value={formData.description}
              onChange={(event) => setFormData({ ...formData, description: event.target.value })}
              required
            />
          </label>

          <label>
            <span>Company</span>
            <input
              type="text"
              value={formData.company}
              onChange={(event) => setFormData({ ...formData, company: event.target.value })}
              required
            />
          </label>

          <label>
            <span>Location</span>
            <input
              type="text"
              value={formData.location}
              onChange={(event) => setFormData({ ...formData, location: event.target.value })}
              required
            />
          </label>

          <button type="submit" className="primary-button" disabled={saving}>
            {saving ? "Posting..." : "Post Job"}
          </button>
        </form>

        {message && (
          <p className={`message ${message.includes("successfully") ? "success" : "error"}`}>{message}</p>
        )}
      </section>

      <section className="feed-card recruiter-jobs">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Posted Jobs</p>
            <h3>Your active listings</h3>
          </div>
        </div>

        {loadingJobs && <p className="support-text">Loading recruiter jobs...</p>}
        {!loadingJobs && jobs.length === 0 && (
          <div className="empty-state">
            <strong>No jobs posted yet</strong>
            <p className="support-text">Use the form to create your first job listing.</p>
          </div>
        )}

        <div className="application-list">
          {jobs.map((job) => (
            <article key={job.id} className="application-row">
              <div>
                <strong>{job.title}</strong>
                <p>
                  {job.company} • {job.location}
                </p>
              </div>
              <button
                type="button"
                className="ghost-button"
                onClick={() => handleViewApplicants(job.id)}
              >
                View Applicants
              </button>
            </article>
          ))}
        </div>
      </section>

      <section className="feed-card recruiter-applicants">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Applicants</p>
            <h3>{selectedJobId ? `Applicants for job #${selectedJobId}` : "Select a job to view applicants"}</h3>
          </div>
        </div>

        {loadingApplicants && <p className="support-text">Loading applicants...</p>}
        {!loadingApplicants && selectedJobId && applicants.length === 0 && (
          <div className="empty-state">
            <strong>No applicants yet</strong>
            <p className="support-text">This role is live, but no one has applied yet.</p>
          </div>
        )}

        <div className="application-list">
          {applicants.map((applicant) => (
            <article key={applicant.id} className="application-row">
              <div>
                <strong>{applicant.applicantName}</strong>
                <p>{applicant.applicantEmail}</p>
                <p>Skills: {applicant.applicantSkills || "No skills added"}</p>
              </div>
              <span className="application-badge">Applied {applicant.appliedDate}</span>
            </article>
          ))}
        </div>
      </section>
    </section>
  );
}
