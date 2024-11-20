import React, { useState } from "react";
import api from "~/axios.config";
import { useNavigate } from "react-router-dom";

function PasswordReset() {
  const [username, setUsername] = useState("");
  const navigate = useNavigate();

  const handleResetRequest = async (e) => {
    e.preventDefault();
    try {
      await api.post("/auth/password", { username });
      navigate("/");
    } catch (error) {
      console.error("Error sending password reset email", error);
    }
  };

  return (
  <div className="password-reset-container main-container">
    <h1>Password reset</h1>
    <form onSubmit={handleResetRequest}>
      <input
        type="text"
        className="form-control"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        required
      />
      <button type="submit" className="btn btn-primary">Send Reset Link</button>
    </form>
  </div>
  );
}

export default PasswordReset;