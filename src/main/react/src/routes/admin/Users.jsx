import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { Link, useNavigate, useParams } from "react-router-dom";
import Loading from "@/components/Loading";

const Users = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/admin/users`);
      setUsers(response.data);
    } catch {}
    setLoading(false);
  };

  const handlePurchases = (userId) => {
    // navigate(`/admin/users/${userId}/purchases`);
  };

  const handleBookings = (userId) => {
    navigate(`/admin/users/${userId}/bookings`);
  };

  if (loading) {
    return <Loading />;
  }

  return (
    users && (
      <div className="admin-users-container main-container">
        <Link to="/admin">Back</Link>
        <h1 className="text-primary">Users</h1>
        <table className="table table-hover">
          <thead>
            <tr>
              <th scope="col">#</th>
              <th scope="col">Firstname</th>
              <th scope="col">Lastname</th>
              <th scope="col">Username</th>
              <th scope="col">Email</th>
              <th scope="col">Role</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => {
              return (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.firstname}</td>
                  <td>{user.lastname}</td>
                  <td>{user.username}</td>
                  <td>{user.email}</td>
                  <td>{user.role.name}</td>
                  <td className="d-flex flex-row gap-2 actions">
                    <button className="btn btn-secondary status" onClick={() => handleBookings(user.id)}>Bookings</button>
                    <button className="btn btn-secondary status" onClick={() => handlePurchases(user.id)}>Purchases</button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    )
  );
};

export default Users;
