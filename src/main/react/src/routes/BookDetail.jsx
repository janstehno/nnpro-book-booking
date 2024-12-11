import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { Link, useNavigate, useParams } from "react-router-dom";
import Loading from "@/components/Loading";

import CartItemType from "@/utils/CartItemType";

const BookDetail = () => {
  const { bookId } = useParams();
  const [detail, setDetail] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
      const fetchBookDetail = async () => {
        const response = await api.get(`/books/${bookId}`);
        setDetail(response.data);
      };
      fetchBookDetail();
    }, [bookId]);

  const addToCart = (item, type) => {
    const quantity = 1;
    const cart = JSON.parse(localStorage.getItem("booking-cart")) || [];
    const existingItemIndex = cart.findIndex(
      (cartItem) => cartItem.id === item.id && cartItem.type === type
    );

    if (existingItemIndex > -1) {
      if(type !== CartItemType.PURCHASE) cart[existingItemIndex].quantity += 1;
    } else {
      cart.push({ ...item, type, quantity });
    }

    localStorage.setItem("booking-cart", JSON.stringify(cart));
    window.dispatchEvent(new Event("cart-updated"));
  };

  if(!detail){
    return <Loading />;
  }

  const { title, description, ebookPrice, physical, physicalCopies, ebook } = detail.book;

  return detail && (
    <div className="detail-container main-container">
      <Link to="/books">Back</Link>
      <h1 className="text-primary">{title}</h1>
      <div className="row">
        <img className="col-2" src="https://placehold.co/100x100" alt="Book cover" />
        <div className="col">
          <p>{description}</p>
          {ebook && <p>${ebookPrice}</p>}
          {physical && <p>Available physical copies: <b>{physicalCopies}</b></p>}
        </div>
      </div>
      <div className="row justify-content-end">
        {physical && (
          <button className="btn btn-primary" onClick={() => {addToCart(detail.book, CartItemType.BOOKING)}}>Zarezervovat</button>
        )}
        {ebook && (
          <button className="btn btn-warning" onClick={() => {addToCart(detail.book, CartItemType.PURCHASE)}}>Objednat</button>
        )}
      </div>
    </div>
  );
}

export default BookDetail;
