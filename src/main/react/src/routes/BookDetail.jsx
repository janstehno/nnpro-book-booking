import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { useNavigate, useParams } from "react-router-dom";

function BookDetail() {
  const { bookId } = useParams();
  const [detail, setDetail] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    api
      .get(`/books/${bookId}`)
      .then((response) => {
        console.log(response);
        setDetail(response.data);
      })
      .catch((error) => {
        console.error("Failed to get detail.", error);
      });
  }, [bookId]);

  if (!detail) {
    return <div>Loading...</div>;
  }

  if (!detail.book) {
    return <div>Detail is not available.</div>;
  }

  return (
    <div className="detail-container main-container">
      <h1>{detail.book.title}</h1>
      <img src="https://placehold.co/100x100" alt="Book cover" />
      <div>
        <p>{detail.book.description}</p>
        <p>${detail.book.ebookPrice}</p>
      </div>
    </div>
  );
}

export default BookDetail;
