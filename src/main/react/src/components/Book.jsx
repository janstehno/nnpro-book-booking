import React, { useState } from 'react';
import { Link } from 'react-router-dom';

const Book = ({book}) => {

  return (
      <div className="col">
        <div className="book-card-container card h-100">
          <img className="card-img-top" src="https://placehold.co/140x210" alt="Book cover" />
          <div className="card-body">
            <h5 className="card-title m-0 pb-3">{book.title}</h5>
            <p className="book-author m-0 pt-2 border-top">{book.author}</p>
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
