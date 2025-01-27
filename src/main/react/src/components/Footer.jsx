import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";

const Footer = () => {
  const navigate = useNavigate();

  return (
    <footer className="footer text-light bg-primary p-4 d-flex flex-wrap gap-2 justify-content-between align-items-center">
        <div className="footer-copyright d-flex justify-content-start align-items-center">
            <p className="m-0">© 2025 Book Booking, All rights reserved.</p>
        </div>
        <div className="footer-socials d-flex flex-wrap gap-1 justify-content-end align-items-center">
            <a href="#"><img className="social-link ms-2" src="/facebook-logo.svg" alt="Facebook"/></a>
            <a href="#"><img className="social-link ms-2" src="/instagram-logo.svg" alt="Instagram"/></a>
        </div>
    </footer>
  );
};

export default Footer;