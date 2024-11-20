import React, { useState, useEffect } from "react";
import api from "../../axios.config.js";
import { useNavigate } from "react-router-dom";

function Profile() {
  const [user, setUser] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await api.get("/profile");
        setUser(response.data);
      } catch (error) {
        console.error("Failed to get user", error);
        navigate("/login");
      }
    };
    fetchUser();
  }, [navigate]);

  return user ? (
    <div className="profile-container main-container">
      <h1>User Profile</h1>
      <p>{user.firstname} {user.lastname}</p>
      <p>{user.email}</p>
      <div className="controls col">
          <div>
              <button onClick={navigate("/profile/update")} className="btn btn-outline-primary">Update Profile</button>
          </div>
          <div>
              <button onClick={navigate("/profile/update-password")} className="btn btn-outline-secondary">Update Password</button>
          </div>
      </div>
    </div>
  ) : (
    <div className="profile-container main-container">Loading...</div>
  );
}

export default Profile;