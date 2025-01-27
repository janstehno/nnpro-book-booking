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

  const addToCart = (item, type, online = false) => {
    const cart = JSON.parse(localStorage.getItem("booking-cart")) || [];
    const existingItemIndex = cart.findIndex(
      (cartItem) => cartItem.id === item.id && cartItem.type === type && cartItem.online === online
    );

    if (existingItemIndex > -1) {
      if (!online && cart[existingItemIndex].quantity < item.physicalCopies) {
        cart[existingItemIndex].quantity += 1;
      }
    } else {
      const newItem = {
        ...item,
        type,
        quantity: online ? 0 : 1,
        online,
      };
      cart.push(newItem);
    }

    localStorage.setItem("booking-cart", JSON.stringify(cart));
    window.dispatchEvent(new Event("cart-updated"));
  };

  if (!detail) return <Loading />;

  const { title, description, ebookPrice, physical, online, physicalCopies, availableCopies, ebook } = detail.book;
  const reviews = detail.reviews;

  return (
      <>
        <div className="detail-container p-4">
          <Link to="/books">Back</Link>
          <h1 className="text-primary">{title}</h1>
          <div className="d-flex flex-wrap row-cols-1 row-cols-sm-2">
            <img src="https://placehold.co/170x210" alt="Book cover" />
            <div className="d-flex flex-column gap-2 justify-content-between">
              <p className="mb-0 ms-sm-4 ms-0 my-sm-0 my-4">{description}</p>
              {online && (
                <button className="mb-0 ms-sm-4 ms-0 my-sm-0 my-4 btn btn-primary text-light d-flex align-self-end"
                  onClick={() => {addToCart(detail.book, CartItemType.ONLINE, true);}}>
                  Book Online
                </button>
              )}
            </div>
          </div>
        </div>
        <div className="detail-container-actions">
            {physical && (
              <div className="d-flex flex-wrap justify-content-around align-items-center p-4 bg-dark text-light gap-3">
                <div className="d-flex flex-column justify-content-center align-items-center">
                    <p className="fw-bold fs-3 m-0 text-center">{physicalCopies}</p>
                    <p className="text-uppercase m-0 text-center">Copies</p>
                </div>
                <div className="d-flex flex-column justify-content-center align-items-center">
                    <p className="fw-bold fs-3 m-0 text-center">{availableCopies}</p>
                    <p className="text-uppercase m-0 text-center">Currently available</p>
                </div>
                <button className="btn btn-warning text-dark" onClick={() => {addToCart(detail.book, CartItemType.BOOKING);}}>
                    Reservation
                </button>
              </div>
            )}
            {ebook && (
              <div className="d-flex flex-wrap justify-content-around align-items-center p-4 bg-light text-dark gap-md-3 gap-0">
                <div className="d-flex flex-column justify-content-center align-items-center">
                    <p className="fw-bold fs-3 m-0 text-center">${ebookPrice}</p>
                    <p className="text-uppercase m-0 text-center">Price</p>
                </div>
                <div className="spacer"></div>
                <button className="btn btn-dark text-light" onClick={() => {addToCart(detail.book, CartItemType.PURCHASE);}}>
                    Buy
                </button>
              </div>
            )}
        </div>
        <div className="p-4">
          <Reviews review={review} reviews={reviews} onUpdate={onUpdate} />
        </div>
    </>
  );
};

export default BookDetail;