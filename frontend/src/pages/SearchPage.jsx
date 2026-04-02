import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api";

export default function SearchPage() {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState({ people: [], jobs: [], companies: [] });
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");

  useEffect(() => {
    loadResults("");
  }, []);

  async function loadResults(searchQuery) {
    setLoading(true);
    try {
      const response = await api.get("/api/search", { params: { query: searchQuery } });
      setResults(response.data.data);
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to load search results.");
    } finally {
      setLoading(false);
    }
  }

  async function handleSearch(event) {
    event.preventDefault();
    await loadResults(query);
  }

  return (
    <section className="page-section row g-4">
      <div className="content-feed col-12 col-xl-8">
        <section className="feed-card">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Search</p>
              <h2>Search people, jobs, and companies in one place</h2>
            </div>
          </div>

          <form className="comment-composer d-flex gap-2" onSubmit={handleSearch}>
            <input
              type="text"
              className="form-control"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Search people, titles, companies, locations"
            />
            <button type="submit" className="btn btn-primary btn-sm">Search</button>
          </form>
        </section>

        {message && <p className="message error">{message}</p>}
        {loading && <p className="support-text">Loading search...</p>}

        {!loading && (
          <>
            <section className="feed-card">
              <div className="section-heading">
                <div>
                  <p className="eyebrow">People</p>
                  <h3>Professionals</h3>
                </div>
              </div>
              <div className="network-grid">
                {results.people.map((person) => (
                  <article key={person.id} className="network-card">
                    <div className="network-card-top">
                      <div className="profile-avatar">{(person.name || "U").slice(0, 1).toUpperCase()}</div>
                      <div>
                        <h3>{person.name}</h3>
                        <p className="network-headline">{person.headline || "Professional profile"}</p>
                        <p className="sidebar-text">{[person.currentCompany, person.location].filter(Boolean).join(" • ")}</p>
                      </div>
                    </div>
                  </article>
                ))}
              </div>
            </section>

            <section className="feed-card">
              <div className="section-heading">
                <div>
                  <p className="eyebrow">Jobs</p>
                  <h3>Open opportunities</h3>
                </div>
              </div>
              <div className="job-grid">
                {results.jobs.map((job) => (
                  <article key={job.id} className="feed-card job-card">
                    <div className="job-card-header">
                      <div>
                        <p className="company-tag">{job.company}</p>
                        <h3>{job.title}</h3>
                      </div>
                      <span className="location-pill">{job.location}</span>
                    </div>
                    <div className="job-chip-group">
                      <span className="job-chip">{job.employmentType || "Role"}</span>
                      <span className="job-chip">{job.seniorityLevel || "Level"}</span>
                      <span className="job-chip">{job.salaryRange || "Salary not listed"}</span>
                    </div>
                  </article>
                ))}
              </div>
            </section>

            <section className="feed-card">
              <div className="section-heading">
                <div>
                  <p className="eyebrow">Companies</p>
                  <h3>Employer brands</h3>
                </div>
                <Link to="/companies" className="text-link link-primary">Open companies</Link>
              </div>
              <div className="network-grid">
                {results.companies.map((company) => (
                  <article key={company.id || company.name} className="network-card">
                    <div>
                      <h3>{company.name}</h3>
                      <p className="network-headline">{company.slogan || company.industry || "Company profile"}</p>
                      <p className="sidebar-text">{company.headquarters || "Headquarters not set"} • {company.activeJobs} jobs</p>
                    </div>
                  </article>
                ))}
              </div>
            </section>
          </>
        )}
      </div>

      <aside className="right-rail col-12 col-xl-4">
        <section className="sidebar-card">
          <p className="sidebar-heading">Search behavior</p>
          <p className="sidebar-text">
            This search layer now spans the social graph, hiring data, and company profiles instead of only matching jobs.
          </p>
        </section>
      </aside>
    </section>
  );
}
