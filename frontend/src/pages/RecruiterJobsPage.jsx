import { useEffect, useState } from "react";
import api from "../api";

const initialForm = {
  title: "",
  description: "",
  company: "",
  location: "",
  employmentType: "",
  seniorityLevel: "",
  salaryRange: ""
};

export default function RecruiterJobsPage() {
  const [formData, setFormData] = useState(initialForm);
  const [jobs, setJobs] = useState([]);
  const [companyForm, setCompanyForm] = useState({
    name: "",
    slogan: "",
    industry: "",
    about: "",
    size: "",
    headquarters: "",
    website: "",
    coverImageUrl: ""
  });
  const [companyProfile, setCompanyProfile] = useState(null);
  const [selectedJobId, setSelectedJobId] = useState(null);
  const [applicants, setApplicants] = useState([]);
  const [message, setMessage] = useState("");
  const [saving, setSaving] = useState(false);
  const [savingCompany, setSavingCompany] = useState(false);
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
    loadCompany();
  }, []);

  async function loadCompany() {
    try {
      const response = await api.get("/api/companies/mine");
      const profile = response.data.data;
      setCompanyProfile(profile);
      setCompanyForm({
        name: profile?.name || "",
        slogan: profile?.slogan || "",
        industry: profile?.industry || "",
        about: profile?.about || "",
        size: profile?.size || "",
        headquarters: profile?.headquarters || "",
        website: profile?.website || "",
        coverImageUrl: profile?.coverImageUrl || ""
      });
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to load company profile.");
    }
  }

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

  async function handleCompanySubmit(event) {
    event.preventDefault();
    setSavingCompany(true);
    setMessage("");

    try {
      const response = await api.post("/api/companies/mine", companyForm);
      setCompanyProfile(response.data.data);
      setMessage(response.data.message || "Company profile saved successfully.");
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to save company profile.");
    } finally {
      setSavingCompany(false);
    }
  }

  return (
    <section className="recruiter-layout">
      <section className="feed-card recruiter-form">
        <p className="eyebrow">Company Page</p>
        <h2>Shape the employer brand behind your jobs</h2>
        <p className="support-text">
          This company profile feeds your recruiter presence, search results, and company discovery across the product.
        </p>

        <form className="form-grid" onSubmit={handleCompanySubmit}>
          <label>
            <span>Company name</span>
            <input
              type="text"
              value={companyForm.name}
              onChange={(event) => setCompanyForm({ ...companyForm, name: event.target.value })}
              required
            />
          </label>

          <label>
            <span>Slogan</span>
            <input
              type="text"
              value={companyForm.slogan}
              onChange={(event) => setCompanyForm({ ...companyForm, slogan: event.target.value })}
            />
          </label>

          <label>
            <span>Industry</span>
            <input
              type="text"
              value={companyForm.industry}
              onChange={(event) => setCompanyForm({ ...companyForm, industry: event.target.value })}
            />
          </label>

          <label>
            <span>Company size</span>
            <input
              type="text"
              value={companyForm.size}
              onChange={(event) => setCompanyForm({ ...companyForm, size: event.target.value })}
            />
          </label>

          <label>
            <span>Headquarters</span>
            <input
              type="text"
              value={companyForm.headquarters}
              onChange={(event) => setCompanyForm({ ...companyForm, headquarters: event.target.value })}
            />
          </label>

          <label>
            <span>Website</span>
            <input
              type="text"
              value={companyForm.website}
              onChange={(event) => setCompanyForm({ ...companyForm, website: event.target.value })}
            />
          </label>

          <label>
            <span>About</span>
            <textarea
              rows="4"
              value={companyForm.about}
              onChange={(event) => setCompanyForm({ ...companyForm, about: event.target.value })}
            />
          </label>

          <button type="submit" className="primary-button" disabled={savingCompany}>
            {savingCompany ? "Saving..." : "Save Company"}
          </button>
        </form>
      </section>

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

          <label>
            <span>Employment type</span>
            <input
              type="text"
              value={formData.employmentType}
              onChange={(event) => setFormData({ ...formData, employmentType: event.target.value })}
              placeholder="Full-time, Contract, Hybrid"
            />
          </label>

          <label>
            <span>Seniority level</span>
            <input
              type="text"
              value={formData.seniorityLevel}
              onChange={(event) => setFormData({ ...formData, seniorityLevel: event.target.value })}
              placeholder="Entry level, Mid-Senior, Staff"
            />
          </label>

          <label>
            <span>Salary range</span>
            <input
              type="text"
              value={formData.salaryRange}
              onChange={(event) => setFormData({ ...formData, salaryRange: event.target.value })}
              placeholder="12-18 LPA"
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
          <div className="mini-stat-grid">
            <div className="mini-stat">
              <span>Company page</span>
              <strong>{companyProfile?.name || "Draft"}</strong>
            </div>
            <div className="mini-stat">
              <span>Open roles</span>
              <strong>{jobs.length}</strong>
            </div>
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
                <p>
                  {[job.employmentType, job.seniorityLevel, job.salaryRange].filter(Boolean).join(" • ")}
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
