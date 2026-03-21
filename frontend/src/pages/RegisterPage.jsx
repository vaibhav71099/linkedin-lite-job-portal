import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api";
import { saveToken, saveUser } from "../utils/auth";

export default function RegisterPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    password: "",
    role: "USER"
  });
  const [otpData, setOtpData] = useState({
    emailOtp: "",
    phoneOtp: ""
  });
  const [step, setStep] = useState("request");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setMessage("");

    try {
      if (step === "request") {
        await api.post("/auth/register", formData);
        setStep("verify");
        setMessage("OTP sent to your email and phone. Enter both codes to continue.");
      } else {
        const response = await api.post("/auth/register/verify", {
          email: formData.email,
          phone: formData.phone,
          emailOtp: otpData.emailOtp,
          phoneOtp: otpData.phoneOtp
        });
        saveToken(response.data.data.token);
        saveUser(response.data.data.user);
        navigate("/dashboard");
      }
    } catch (error) {
      setMessage(error.response?.data?.message || "Registration failed.");
    } finally {
      setLoading(false);
    }
  }

  async function handleResendOtp() {
    setLoading(true);
    setMessage("");

    try {
      await api.post("/auth/register", formData);
      setMessage("New OTP sent. Please check your email and phone.");
    } catch (error) {
      setMessage(error.response?.data?.message || "Failed to resend OTP.");
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
          <h2>Make the most of your professional life</h2>
          <p className="support-text">Create an account and start applying or hiring.</p>
        </div>

        <form className="form-grid" onSubmit={handleSubmit}>
          <label>
            <span>Name</span>
            <input
              type="text"
              value={formData.name}
              onChange={(event) => setFormData({ ...formData, name: event.target.value })}
              disabled={step === "verify"}
              required
            />
          </label>

          <label>
            <span>Email</span>
            <input
              type="email"
              value={formData.email}
              onChange={(event) => setFormData({ ...formData, email: event.target.value })}
              disabled={step === "verify"}
              required
            />
          </label>

          <label>
            <span>Phone</span>
            <input
              type="tel"
              placeholder="+91XXXXXXXXXX"
              value={formData.phone}
              onChange={(event) => setFormData({ ...formData, phone: event.target.value })}
              disabled={step === "verify"}
              required
            />
          </label>

          <label>
            <span>Password</span>
            <input
              type="password"
              value={formData.password}
              onChange={(event) => setFormData({ ...formData, password: event.target.value })}
              disabled={step === "verify"}
              required
            />
          </label>

          <label>
            <span>Role</span>
            <select
              value={formData.role}
              onChange={(event) => setFormData({ ...formData, role: event.target.value })}
              disabled={step === "verify"}
            >
              <option value="USER">User</option>
              <option value="RECRUITER">Recruiter</option>
            </select>
          </label>

          {step === "verify" && (
            <>
              <label>
                <span>Email OTP</span>
                <input
                  type="text"
                  inputMode="numeric"
                  value={otpData.emailOtp}
                  onChange={(event) => setOtpData({ ...otpData, emailOtp: event.target.value })}
                  required
                />
              </label>

              <label>
                <span>Phone OTP</span>
                <input
                  type="text"
                  inputMode="numeric"
                  value={otpData.phoneOtp}
                  onChange={(event) => setOtpData({ ...otpData, phoneOtp: event.target.value })}
                  required
                />
              </label>
            </>
          )}

          <button type="submit" className="primary-button" disabled={loading}>
            {loading
              ? step === "request"
                ? "Sending OTP..."
                : "Verifying OTP..."
              : step === "request"
                ? "Send OTP"
                : "Verify & Create Account"}
          </button>

          {step === "verify" && (
            <button
              type="button"
              className="ghost-button"
              disabled={loading}
              onClick={handleResendOtp}
            >
              Resend OTP
            </button>
          )}
        </form>

        {message && <p className="message error">{message}</p>}

        <p className="switch-link">
          Already on LinkedIn Lite? <Link to="/login">Sign in</Link>
        </p>
      </section>
    </section>
  );
}
