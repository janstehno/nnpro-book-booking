import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { useNavigate } from "react-router-dom";

const Profile = () => {
  const [user, setUser] = useState(null);
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

  const handleHistory = () => {
    navigate("/user/history");
  };

  const handleUpdateProfile = () => {
    navigate("/user/update");
  };

  const handleUpdateProfilePassword = () => {
    navigate("/user/update-password");
  };

  const handleManagement = () => {
    navigate("/admin");
  };

  return (
    user && (
      <div className="profile-container main-container">
        <h1 className="text-primary">Profile</h1>
        <p>{user.firstname} {user.lastname}</p>
        <p>{user.email}</p>
        <div className="controls col">
          <div>
            <button onClick={handleHistory} className="btn btn-primary">History</button>
          </div>
          <div>
            <button onClick={handleUpdateProfile} className="btn btn-outline-primary">Update Profile</button>
          </div>
          <div>
            <button onClick={handleUpdateProfilePassword} className="btn btn-outline-secondary">Update Password</button>
          </div>
          {user.role === "ADMIN" && (
            <div>
              <button onClick={handleManagement} className="btn btn-danger text-light">Management</button>
            </div>
          )}
        </div>
      </div>
    )
  );
};

export default Profile;