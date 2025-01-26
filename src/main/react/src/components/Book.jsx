import React, { useState } from 'react';
import { Link } from 'react-router-dom';

const Book = ({book}) => {

  return (
      <div className="col">
        <div className="book-card-container card h-100">
          <img className="card-img-top" src="https://placehold.co/170x210" alt="Book cover" />
          <div className="card-body">
            <h5 className="card-title m-0">{book.title}</h5>
            <p className="book-author m-0 pb-2">{book.author}</p>
            {book.price && (
              <p className="book-price m-0 pt-2 border-top">${book.price}</p>
            )}
            <div className="book-availability">
              <p className="book-price m-0">Available: <b>{book.available}</b></p>
            </div>
          </div>
          <a className="text-decoration-none text-light" href={`/books/${book.id}`}>
            <div className="card-footer bg-primary">
                <p className="m-0 text-center">Detail</p>
            </div>
          </a>
        </div>
      </div>
  );
}

export default Book;
