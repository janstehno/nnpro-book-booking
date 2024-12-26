import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { Link, useNavigate, useParams } from "react-router-dom";
import Loading from "@/components/Loading";

import CartItemType from "@/utils/CartItemType";
import Review from "@/components/Review";

const BookDetailReviews = ({ review, reviews, onUpdate }) => {
  const { bookId } = useParams();
  const [editedText, setEditedText] = useState("");
  const [editedRating, setEditedRating] = useState(0);
  const navigate = useNavigate();

  const handleSave = async () => {
    try {
      const response = await api.post(`/books/${bookId}/review`, {
        text: editedText,
        rating: editedRating,
      });
    } catch {}
  };

  const renderRating = (currentRating, interactive = false) => {
    const maxRating = 5;
    const full = "/star-filled.svg";
    const empty = "/star-empty.svg";

    const handleClick = (newRating) => {
      if (interactive) setEditedRating(newRating);
    };

    return (
      <div className="rating d-flex flex-row">
        {Array.from({ length: maxRating }, (_, index) => (
          <img
            key={index}
            src={index < currentRating ? full : empty}
            alt={index < currentRating ? "Filled Star" : "Empty Star"}
            className={`star me-1 ${interactive ? "cursor-pointer" : ""}`}
            onClick={() => handleClick(index + 1)}
          />
        ))}
      </div>
    );
  };

  if (!reviews) return <Loading />;

  return (
      <div className="reviews-section mt-4">
        <h3>Reviews</h3>
        {!review && localStorage["token"] && (
          <form className="form mb-5">
            <h5>Add review</h5>
            <div className="rating mb-1">{ renderRating(editedRating, true) }</div>
            <input
              className="form-control mb-2"
              value={editedText}
              onChange={(e) => setEditedText(e.target.value)}
            />
            <button className="btn btn-success btn-sm me-2" onClick={handleSave}>Submit</button>
          </form>
        )}
        { review && (
          <Review
            key={review.id}
            bookId={bookId}
            review={review}
            editable={true}
            onUpdate={onUpdate}
          />
        )}
        { reviews.length > (review ? 1 : 0) ? (
          reviews.filter((r) => !review || r.id !== review.id).map((r) => (
            <Review
              key={r.id}
              bookId={bookId}
              review={r}
              editable={false}
              onUpdate={onUpdate}
            />
          ))
        ) : review ? <></> : <p>No reviews available for this book.</p> }
      </div>
  );
};

export default BookDetailReviews;