import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { Link, useNavigate, useParams } from "react-router-dom";

import CartItem from "@/utils/CartItem";

function BookDetail() {
  const { bookId } = useParams();
  const [detail, setDetail] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    api
      .get(`/books/${bookId}`)
      .then((response) => {
        setDetail(response.data);
      })
      .catch((error) => {
        console.error("Failed to get detail.", error);
      });
  }, [bookId]);

  const addToCart = (item, type) => {
    const quantity = 1;
    const cart = JSON.parse(localStorage.getItem("booking-cart")) || [];
    const existingItemIndex = cart.findIndex(
      (cartItem) => cartItem.id === item.id && cartItem.type === type
    );

    if (existingItemIndex > -1) {
      if(type !== CartItem.PURCHASE) cart[existingItemIndex].quantity += 1;
    } else {
      cart.push({ ...item, type, quantity });
    }

    localStorage.setItem("booking-cart", JSON.stringify(cart));
  };

  if (!detail) {
    return <div>Loading...</div>;
  }

  if (!detail.book) {
    return <div>Detail is not available.</div>;
  }

  const { title, description, ebookPrice, physical, ebook } = detail.book;

  return (
    <div className="detail-container main-container">
      <Link to="/books">Back</Link>
      <h1 className="text-primary">{title}</h1>
      <div className="row">
        <img className="col-2" src="https://placehold.co/100x100" alt="Book cover" />
        <div className="col">
          <p>{description}</p>
          {ebook && <p>${ebookPrice}</p>}
        </div>
      </div>
      <div className="row justify-content-end">
        {physical && (
          <button className="btn btn-primary" onClick={() => {addToCart(detail.book, CartItem.BOOKING)}}>Zarezervovat</button>
        )}
        {ebook && (
          <button className="btn btn-warning" onClick={() => {addToCart(detail.book, CartItem.PURCHASE)}}>Objednat</button>
        )}
      </div>
    </div>
  );
}

export default BookDetail;
