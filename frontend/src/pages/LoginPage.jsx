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
    <section className="auth-page d-flex align-items-center justify-content-center min-vh-100 bg-light">
      <section className="auth-panel auth-card card shadow-sm p-4">
        <div>
          <div className="auth-brand">
            <div className="linkedin-logo">in</div>
            <strong>LinkedIn Lite</strong>
          </div>
          <h2>Sign in</h2>
          <p className="support-text">Stay updated on your professional world.</p>
        </div>

        <form className="form-grid d-grid gap-3" onSubmit={handleSubmit}>
          <label className="d-grid gap-2">
            <span className="form-label">Email</span>
            <input
              type="email"
              className="form-control"
              value={formData.email}
              onChange={(event) => setFormData({ ...formData, email: event.target.value })}
              required
            />
          </label>

          <label className="d-grid gap-2">
            <span className="form-label">Password</span>
            <input
              type="password"
              className="form-control"
              value={formData.password}
              onChange={(event) => setFormData({ ...formData, password: event.target.value })}
              required
            />
          </label>

          <button type="submit" className="btn btn-primary w-100" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        {message && <div className="alert alert-danger mb-0">{message}</div>}

        <p className="switch-link text-center">
          New to LinkedIn Lite? <Link className="link-primary" to="/register">Join now</Link>
        </p>
      </section>
    </section>
  );
}
