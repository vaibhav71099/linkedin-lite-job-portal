import { useEffect, useState } from "react";
import api from "../api";
import { getUser, saveUser } from "../utils/auth";

export default function ProfilePage() {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
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
      <section className="feed-card profile-editor">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Profile</p>
            <h2>Build a profile recruiters can scan in seconds</h2>
            <p className="support-text">
              Keep your headline, summary, and skills current so the rest of the platform feels complete.
            </p>
          </div>
        </div>

        {loading ? (
          <p className="support-text">Loading profile...</p>
        ) : (
          <form className="form-grid" onSubmit={handleSubmit}>
            <label>
              <span>Name</span>
              <input
                type="text"
                value={formData.name}
                onChange={(event) => setFormData({ ...formData, name: event.target.value })}
                required
              />
            </label>

            <label>
              <span>Email</span>
              <input
                type="email"
                value={formData.email}
                onChange={(event) => setFormData({ ...formData, email: event.target.value })}
                required
              />
            </label>

            <label>
              <span>Bio</span>
              <textarea
                rows="5"
                value={formData.bio}
                onChange={(event) => setFormData({ ...formData, bio: event.target.value })}
                placeholder="Write a short professional summary"
              />
            </label>

            <label>
              <span>Skills</span>
              <textarea
                rows="4"
                value={formData.skills}
                onChange={(event) => setFormData({ ...formData, skills: event.target.value })}
                placeholder="Spring Boot, React, MySQL, REST APIs"
              />
            </label>

            <button type="submit" className="primary-button" disabled={saving}>
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
