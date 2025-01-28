import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { useLocation, useNavigate } from "react-router-dom";

const PasswordResetSubmit = () => {
  const [token, setToken] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const tokenFromUrl = queryParams.get("token");
    if (tokenFromUrl) {
      setToken(tokenFromUrl);
    }
  }, [location]);

  const handlePasswordReset = async (e) => {
    e.preventDefault();

    if (newPassword !== confirmPassword) {
      throw new Error("Passwords do not match");
    }

    try {
      await api.post("/auth/password/reset", { token, newPassword, confirmPassword });
      navigate("/login");
    } catch {}
  };

  return (
    <div className="password-reset-submit-container main-container d-flex flex-column justify-content-center align-items-center">
      <h1 className="text-primary">Reset Password</h1>
      <form className="d-flex flex-column justify-content-center align-items-center" onSubmit={handlePasswordReset}>
        <input
          type="password"
          className="form-control"
          placeholder="New Password"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          required
        />
        <input
          type="password"
          className="form-control"
          placeholder="Confirm New Password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          required
        />
        <button type="submit" className="btn btn-primary">Reset Password</button>
      </form>
    </div>
  );
}

export default PasswordResetSubmit;