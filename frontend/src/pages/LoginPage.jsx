import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api";
import { saveToken, saveUser } from "../utils/auth";

export default function LoginPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ email: "", password: "" });
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setMessage("");

    try {
      const response = await api.post("/auth/login", formData);
      saveToken(response.data.data.token);
      saveUser(response.data.data.user);
      navigate("/dashboard");
    } catch (error) {
      setMessage(error.response?.data?.message || "Login failed.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="auth-page">
      <section className="auth-panel auth-card">
        <div>
          <div className="auth-brand">
            <div className="linkedin-logo">in</div>
            <strong>LinkedIn Lite</strong>
          </div>
          <h2>Sign in</h2>
          <p className="support-text">Stay updated on your professional world.</p>
        </div>

        <form className="form-grid" onSubmit={handleSubmit}>
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
            <span>Password</span>
            <input
              type="password"
              value={formData.password}
              onChange={(event) => setFormData({ ...formData, password: event.target.value })}
              required
            />
          </label>

          <button type="submit" className="primary-button" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        {message && <p className="message error">{message}</p>}

        <p className="switch-link">
          New to LinkedIn Lite? <Link to="/register">Join now</Link>
        </p>
      </section>
    </section>
  );
}
