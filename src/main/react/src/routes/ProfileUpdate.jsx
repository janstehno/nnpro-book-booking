import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { Link, useNavigate } from "react-router-dom";

const ProfileUpdate = () => {
  const [user, setUser] = useState({ firstname: "", lastname: "", email: "" });
  const navigate = useNavigate();

  useEffect(() => {
    fetchUser();
  }, [navigate]);

  const fetchUser = async () => {
    try {
      const response = await api.get("/user");
      setUser(response.data);
    } catch {}
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUser((prevUser) => ({...prevUser, [name]: value}));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.put("/user", user);
      navigate("/user");
    } catch {}
  };

  return (
    <div className="profile-update-container main-container">
      <Link to="/user">Back</Link><h1 className="text-primary">Update Profile</h1>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>First Name</label>
          <input
            type="text"
            name="firstname"
            className="form-control"
            value={user.firstname}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label>Last Name</label>
          <input
            type="text"
            name="lastname"
            className="form-control"
            value={user.lastname}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            name="email"
            className="form-control"
            value={user.email}
            onChange={handleChange}
          />
        </div>
        <button type="submit" className="btn btn-primary">Update</button>
      </form>
    </div>
  );
}

export default ProfileUpdate;