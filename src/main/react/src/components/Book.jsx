import React, { useState } from 'react';
import { Link } from 'react-router-dom';

const Book = ({book}) => {

  return (
    <div className="book-card-container card">
      <img className="card-img-top" src="https://placehold.co/100x100" alt="Book cover" />
      <div className="card-body">
        <h3 className="card-title">{book.title}</h3>
        <p className="book-price">{book.description}</p>
        {book.ebook && <p className="card-text">${book.ebookPrice}</p>}
        <Link className="btn btn-primary" to={`/books/${book.id}`}>Detail</Link>
      </div>
    </div>
  );
}

export default Book;
