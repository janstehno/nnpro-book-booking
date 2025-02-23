import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";

const Navbar = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [itemsInCart, setItemsInCart] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
      const token = localStorage.getItem("token");
      setIsLoggedIn(token != null ? true : false);

      const cart = JSON.parse(localStorage.getItem("booking-cart")) || [];
      setItemsInCart(cart.length > 0);

      const handleCartUpdated = () => {
        const cart = JSON.parse(localStorage.getItem("booking-cart")) || [];
        setItemsInCart(cart.length > 0);
      };

      window.addEventListener("cart-updated", handleCartUpdated);
      return () => {
        window.removeEventListener("cart-updated", handleCartUpdated);
      };
    }, [isLoggedIn, itemsInCart, navigate]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("booking-cart");
    setIsLoggedIn(false);
    navigate("/login");
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light">
      <div className="container-fluid">
        <Link className="navbar-brand" to="/"><img className="navbar-logo" src="/favicon.ico" alt="Book Booking logo" /></Link>
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto">
            <li className="nav-item">
              <Link className="nav-link" to="/books">Books</Link>
            </li>
            {!isLoggedIn ? (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/login">Login</Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/register">Register</Link>
                </li>
              </>
            ) : (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/user">Profile</Link>
                </li>
                <li className="nav-item">
                  <button className="btn btn-link nav-link" onClick={handleLogout}>Logout</button>
                </li>
              </>
            )}
            {isLoggedIn && itemsInCart && (
              <>
                <li className="nav-item">
                  <Link className="nav-link shopping-cart" to="/cart"><img src="/shopping-cart.png" alt="Cart" /></Link>
                </li>
              </>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;