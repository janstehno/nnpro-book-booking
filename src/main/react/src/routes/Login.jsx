import React, { useState } from "react";
import api from "~/axios.config";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [credentials, setCredentials] = useState({ username: "", password: "" });
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post("/auth/login", credentials);
      localStorage.setItem("token", response.data.token);
      navigate("/user");
    } catch {}
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCredentials({...credentials, [name]: value});
  };

  return (
    <div className="login-container main-container">
      <h1 className="text-primary">Login</h1>
      <form onSubmit={handleLogin}>
        <input
          type="text"
          className="form-control"
          placeholder="Username"
          name="username"
          value={credentials.username}
          onChange={handleChange}
          required
        />
        <input
          type="password"
          className="form-control"
          placeholder="Password"
          name="password"
          value={credentials.password}
          onChange={handleChange}
          required
        />
        <button type="submit" className="btn btn-primary">Login</button>
      </form>
      <a href="/password">Reset password</a>
    </div>
  );
}

export default Login;