import { useEffect, useState } from "react";
import api from "../api";

export default function CompaniesPage() {
  const [companies, setCompanies] = useState([]);
  const [query, setQuery] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadCompanies("");
  }, []);

  async function loadCompanies(searchQuery) {
    setLoading(true);
    try {
      const response = await api.get("/api/companies", { params: { query: searchQuery } });
      setCompanies(response.data.data);
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to load companies.");
    } finally {
      setLoading(false);
    }
  }

  async function handleSearch(event) {
    event.preventDefault();
    await loadCompanies(query);
  }

  return (
    <section className="page-section row g-4">
      <div className="content-feed col-12 col-xl-8">
        <section className="feed-card">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Companies</p>
              <h2>Browse employer brands and active hiring teams</h2>
              <p className="support-text">
                Company pages add context beyond a flat job list: what the company does, where it is based, and how active it is.
              </p>
            </div>
          </div>

          <form className="comment-composer d-flex gap-2" onSubmit={handleSearch}>
            <input
              type="text"
              className="form-control"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Search companies or industries"
            />
            <button type="submit" className="btn btn-primary btn-sm">Search</button>
          </form>
        </section>

        {message && <p className="message error">{message}</p>}
        {loading && <p className="support-text">Loading companies...</p>}

        <div className="network-grid">
          {!loading && companies.map((company) => (
            <article key={company.id || company.name} className="feed-card company-card">
              <div className="company-cover">
                <div className="company-cover-copy">
                  <p className="eyebrow">Company</p>
                  <h3>{company.name || "Your company profile"}</h3>
                  <p className="support-text">{company.slogan || "Add a slogan that sounds like a real employer brand."}</p>
                </div>
              </div>
              <div className="company-meta-grid">
                <div className="mini-stat">
                  <span>Industry</span>
                  <strong>{company.industry || "Not set"}</strong>
                </div>
                <div className="mini-stat">
                  <span>Headquarters</span>
                  <strong>{company.headquarters || "Not set"}</strong>
                </div>
                <div className="mini-stat">
                  <span>Open roles</span>
                  <strong>{company.activeJobs}</strong>
                </div>
              </div>
              <p className="job-description">{company.about || "No company summary yet."}</p>
            </article>
          ))}
        </div>

        {!loading && companies.length === 0 && (
          <section className="feed-card empty-card">
            <strong>No companies matched your search</strong>
            <p className="support-text">Try a broader keyword or wait until more recruiter teams create company pages.</p>
          </section>
        )}
      </div>

      <aside className="right-rail col-12 col-xl-4">
        <section className="sidebar-card">
          <p className="sidebar-heading">Why company pages matter</p>
          <p className="sidebar-text">
            Candidates respond better when a job listing is anchored to a company brand, industry, and clear employer story.
          </p>
        </section>
      </aside>
    </section>
  );
}
