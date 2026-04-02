import { useEffect, useState } from "react";
import api from "../api";
import { getUser, saveUser } from "../utils/auth";

export default function ProfilePage() {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    headline: "",
    location: "",
    currentCompany: "",
    education: "",
    bio: "",
    skills: ""
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    const currentUser = getUser();

    if (currentUser) {
      setFormData({
        name: currentUser.name || "",
        email: currentUser.email || "",
        headline: currentUser.headline || "",
        location: currentUser.location || "",
        currentCompany: currentUser.currentCompany || "",
        education: currentUser.education || "",
        bio: currentUser.bio || "",
        skills: currentUser.skills || ""
      });
      setLoading(false);
    }

    async function loadProfile() {
      try {
        const response = await api.get("/api/users/me");
        const profile = response.data.data;
        saveUser(profile);
        setFormData({
          name: profile.name || "",
          email: profile.email || "",
          headline: profile.headline || "",
          location: profile.location || "",
          currentCompany: profile.currentCompany || "",
          education: profile.education || "",
          bio: profile.bio || "",
          skills: profile.skills || ""
        });
      } catch (error) {
        setMessage(error.response?.data?.message || "Unable to load profile.");
      } finally {
        setLoading(false);
      }
    }

    loadProfile();
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();
    setSaving(true);
    setMessage("");

    try {
      const response = await api.put("/api/users/me", formData);
      saveUser(response.data.data);
      setMessage(response.data.message || "Profile updated successfully.");
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to update profile.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <section className="profile-layout">
      <section className="feed-card profile-showcase">
        <div className="profile-cover profile-cover-large" />
        <div className="profile-showcase-body">
          <div className="profile-avatar large">
            {(formData.name || "U").slice(0, 1).toUpperCase()}
          </div>
          <div className="profile-showcase-copy">
            <h2>{formData.name || "Your Name"}</h2>
            <p className="profile-role-label">{formData.headline || "Add a headline that explains what you do."}</p>
            <p className="sidebar-text">
              {[formData.currentCompany, formData.location, formData.education].filter(Boolean).join(" • ") || "Company, location, and education make your profile read like a real professional identity."}
            </p>
          </div>
        </div>
      </section>

      <section className="feed-card profile-editor">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Profile</p>
            <h2>Build a profile people can trust at a glance</h2>
            <p className="support-text">
              Fill the core fields that make LinkedIn-style profiles feel complete: headline, location, company, education, about, and skills.
            </p>
          </div>
        </div>

        {loading ? (
          <p className="support-text">Loading profile...</p>
        ) : (
          <form className="form-grid d-grid gap-3" onSubmit={handleSubmit}>
            <label>
              <span>Name</span>
              <input
                type="text"
                className="form-control"
                value={formData.name}
                onChange={(event) => setFormData({ ...formData, name: event.target.value })}
                required
              />
            </label>

            <label>
              <span>Email</span>
              <input
                type="email"
                className="form-control"
                value={formData.email}
                onChange={(event) => setFormData({ ...formData, email: event.target.value })}
                required
              />
            </label>

            <label>
              <span>Headline</span>
              <input
                type="text"
                className="form-control"
                value={formData.headline}
                onChange={(event) => setFormData({ ...formData, headline: event.target.value })}
                placeholder="Software engineer building customer-facing products"
              />
            </label>

            <label>
              <span>Location</span>
              <input
                type="text"
                className="form-control"
                value={formData.location}
                onChange={(event) => setFormData({ ...formData, location: event.target.value })}
                placeholder="Pune, Maharashtra, India"
              />
            </label>

            <label>
              <span>Current company</span>
              <input
                type="text"
                className="form-control"
                value={formData.currentCompany}
                onChange={(event) => setFormData({ ...formData, currentCompany: event.target.value })}
                placeholder="Current workplace or venture"
              />
            </label>

            <label>
              <span>Education</span>
              <input
                type="text"
                className="form-control"
                value={formData.education}
                onChange={(event) => setFormData({ ...formData, education: event.target.value })}
                placeholder="University or program"
              />
            </label>

            <label>
              <span>About</span>
              <textarea
                rows="5"
                className="form-control"
                value={formData.bio}
                onChange={(event) => setFormData({ ...formData, bio: event.target.value })}
                placeholder="Write a short professional summary"
              />
            </label>

            <label>
              <span>Skills</span>
              <textarea
                rows="4"
                className="form-control"
                value={formData.skills}
                onChange={(event) => setFormData({ ...formData, skills: event.target.value })}
                placeholder="Spring Boot, React, MySQL, REST APIs"
              />
            </label>

            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? "Saving..." : "Save Profile"}
            </button>
          </form>
        )}

        {message && (
          <p className={`message ${message.includes("successfully") ? "success" : "error"}`}>{message}</p>
        )}
      </section>
    </section>
  );
}
