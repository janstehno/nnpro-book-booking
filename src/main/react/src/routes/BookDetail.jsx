import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { Link, useNavigate, useParams } from "react-router-dom";
import Loading from "@/components/Loading";

import CartItemType from "@/utils/CartItemType";
import Reviews from "@/components/BookDetailReviews";
import Review from "@/components/Review";

const BookDetail = () => {
  const { bookId } = useParams();
  const [detail, setDetail] = useState(null);
  const [review, setReview] = useState(null);
  const [editedText, setEditedText] = useState("");
  const [editedRating, setEditedRating] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    fetchBookDetail();
    fetchBookReview();
  }, []);

  const fetchBookDetail = async () => {
    try {
      const response = await api.get(`/books/${bookId}`);
      setDetail(response.data);
    } catch {}
  };

  const fetchBookReview = async () => {
    try {
      const response = await api.get(`/books/${bookId}/review`);
      setReview(response.data);
    } catch {}
  };

  const onUpdate = (review) => {
    setReview(review);
  }

  const addToCart = (item, type) => {
    const quantity = 1;
    const cart = JSON.parse(localStorage.getItem("booking-cart")) || [];
    const existingItemIndex = cart.findIndex(
      (cartItem) => cartItem.id === item.id && cartItem.type === type
    );

    if (existingItemIndex > -1) {
      if (cart[existingItemIndex].quantity < item.physicalCopies && type !== CartItemType.PURCHASE) {
        cart[existingItemIndex].quantity += 1;
      }
    } else {
      cart.push({ ...item, type, quantity });
    }

    localStorage.setItem("booking-cart", JSON.stringify(cart));
    window.dispatchEvent(new Event("cart-updated"));
  };

  if (!detail) return <Loading />;

  const { title, description, ebookPrice, physical, physicalCopies, availableCopies, ebook } = detail.book;
  const reviews = detail.reviews;

  return (
    <div className="detail-container main-container">
      <Link to="/books">Back</Link>
      <h1 className="text-primary">{title}</h1>
      <div className="d-flex flex-row">
        <img className="col-2 me-3" src="https://placehold.co/140x210" alt="Book cover" />
        <div className="col">
          <p className="mb-4">{description}</p>
        </div>
      </div>
      <div className="actions">
        <div className="d-flex flex-row justify-content-end align-items-center mb-2">
          {physical && (
            <>
              <p className="me-4">COPIES <b className="tag copies">{physicalCopies}</b></p>
              <p className="me-4">CURRENTLY AVAILABLE <b className="tag copies">{availableCopies}</b></p>
              <button className="btn btn-primary" onClick={() => {addToCart(detail.book, CartItemType.BOOKING);}}>
                Reservation
              </button>
            </>
          )}
        </div>
        <div className="d-flex flex-row justify-content-end align-items-center mb-2">
          {ebook && (
            <>
              <p className="me-4">PRICE <b className="tag price">${ebookPrice}</b></p>
              <button className="btn btn-warning" onClick={() => {addToCart(detail.book, CartItemType.PURCHASE);}}>
                Buy
              </button>
            </>
          )}
        </div>
      </div>

      <Reviews review={review} reviews={reviews} onUpdate={onUpdate} />
    </div>
  );
};

export default BookDetail;