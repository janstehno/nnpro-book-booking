import React, { useState } from "react";
import api from "~/axios.config";

const Review = ({ bookId, review, editable, onUpdate }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editedText, setEditedText] = useState(review.text);
  const [editedRating, setEditedRating] = useState(review.rating);

  const handleSave = async () => {
    try {
      const response = await api.put(`/books/${bookId}/review`, {
        text: editedText,
        rating: editedRating,
      });
      if(onUpdate) onUpdate(response.data);
      setIsEditing(false);
    } catch {}
  };

  const handleDelete = async () => {
      try {
        await api.delete(`/books/${bookId}/review`);
        if(onUpdate) onUpdate(null);
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

  return (
    <div className="review card my-3">
      <p className="date card-header">{new Date(review.date).toLocaleDateString()}</p>
      <div className="card-body d-flex flex-row align-items-center">
        <div className="col">
          <p className="author mb-3">
            {review.firstname.toUpperCase()} {review.lastname.toUpperCase()}
          </p>
          <div className="rating mb-1">
            { isEditing ? renderRating(editedRating, true) : renderRating(review.rating) }
          </div>
          {isEditing ? (
            <input
              className="form-control mb-2"
              value={editedText}
              onChange={(e) => setEditedText(e.target.value)}
            />
          ) : (
            <p className="card-text">{review.text}</p>
          )}
        </div>
        {editable && (
          <div className="col-2 d-flex justify-content-end">
            {isEditing ? (
              <>
                <button className="btn btn-success btn-sm me-2" onClick={handleSave}>Save</button>
                <button
                  className="btn btn-secondary btn-sm"
                  onClick={() => {
                    setIsEditing(false);
                    setEditedText(review.text);
                    setEditedRating(review.rating);
                  }}
                >Cancel</button>
              </>
            ) : (
              <>
                <button className="btn btn-primary btn-sm" onClick={() => setIsEditing(true)}>Edit</button>
                <button className="btn btn-danger btn-sm" onClick={handleDelete}>Delete</button>
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Review;