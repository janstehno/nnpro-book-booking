import React, { useState } from "react";
import api from "~/axios.config";
import { useNavigate } from "react-router-dom";

const Register = () => {
  const [formData, setFormData] = useState({
    firstname: "", lastname: "",
    email: "", username: "", password: ""
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({...formData, [name]: value});
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      await api.post("/auth/register", formData);
      navigate("/login");
    } catch {}
  };

  return (
    <div className="register-container main-container">
      <h1 className="text-primary">Register</h1>
      <form onSubmit={handleRegister}>
        <input
          type="text"
          className="form-control"
          placeholder="First Name"
          name="firstname"
          value={formData.firstname}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          className="form-control"
          placeholder="Last Name"
          name="lastname"
          value={formData.lastname}
          onChange={handleChange}
          required
        />
        <input
          type="email"
          className="form-control"
          placeholder="Email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          className="form-control"
          placeholder="Username"
          name="username"
          value={formData.username}
          onChange={handleChange}
          required
        />
        <input
          type="password"
          className="form-control"
          placeholder="Password"
          name="password"
          value={formData.password}
          onChange={handleChange}
          required
        />
        <button type="submit" className="btn btn-primary">Register</button>
      </form>
    </div>
  );
}

export default Register;
