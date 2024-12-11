import React, { useState } from "react";
import api from "../../axios.config.js";
import { Link, useNavigate } from "react-router-dom";

const UpdatePassword = () => {
  const [passwordData, setPasswordData] = useState({ oldPassword: "", password: "" });
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setPasswordData((prevData) => ({...prevData, [name]: value}));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.put("/user/password", passwordData);
      navigate("/user");
    } catch {}
  };

  return (
    <div className="profile-update-password-container main-container">
      <Link to="/user">Back</Link><h1 className="text-primary">Update Password</h1>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Old Password</label>
          <input
            type="password"
            name="oldPassword"
            className="form-control"
            value={passwordData.oldPassword}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label>New Password</label>
          <input
            type="password"
            name="password"
            className="form-control"
            value={passwordData.password}
            onChange={handleChange}
          />
        </div>
        <button type="submit" className="btn btn-primary">Update Password</button>
      </form>
    </div>
  );
}

export default UpdatePassword;