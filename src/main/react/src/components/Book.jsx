import React from "react";

const Book = ({book}) => {
  return (
      <div className="col">
        <div className="book-card-container card h-100">
          <img className="card-img-top" src="https://placehold.co/170x210" alt="Book cover" />
          <div className="card-body">
            <h5 className="card-title m-0">{book.title}</h5>
            <p className="book-author m-0">{book.author}</p>
            <div className="my-2 border-top"></div>
            {book.ebook && book.price && (
              <p className="book-price m-0">${book.price}</p>
            )}
            <div className="book-availability d-flex flex-wrap justify-content-between align-items-center">
              <p className="book-price m-0">Available: <b>{book.available}</b></p>
              {book.online && ( <img className="book-online" src="online.svg" alt="Available online"/> )}
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
