import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";

const Banner = () => {
  const navigate = useNavigate();

  return (
    <div className="home-container-banner">
        <div
            className="home-container-banner-overshadow overshadow p-4 d-flex flex-column justify-content-center
            align-items-center text-white">
            <div className="d-flex flex-column justify-content-center align-items-center">
                <h2 className="section-title my-4 fs-1 text-uppercase text-center">Subscribe to newsletter</h2>
                <p className="text-center">
                    Stay updated! Subscribe to our newsletter for the latest books, exclusive
                    offers, and more.
                </p>
                <div className="d-flex flex-wrap justify-content-center align-items-center gap-2 w-100">
                    <input className="form-control w-75" type="text" placeholder="Your Email"/>
                    <button className="btn btn-warning">
                        <a className="text-dark text-decoration-none" href="#">Subscribe</a>
                    </button>
                </div>
            </div>
        </div>
    </div>
  );
};

export default Banner;