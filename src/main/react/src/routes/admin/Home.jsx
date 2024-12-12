import { Link, useNavigate } from "react-router-dom";

const Home = () => {
  const navigate = useNavigate();

  const handleUsers = () => {
    navigate("/admin/users");
  };

  const handleBooks = () => {
    // navigate("/admin/books");
  };

  return (
    <div className="profile-container main-container">
      <h1 className="text-primary">Management</h1>
      <Link to="/user">Back</Link>
      <div className="controls col">
          <div>
              <button onClick={handleUsers} className="btn btn-outline-primary">Users</button>
          </div>
          <div>
              <button onClick={handleBooks} className="btn btn-outline-primary">Books</button>
          </div>
      </div>
    </div>
    );
};

export default Home;
