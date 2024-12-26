import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import Book from "@/components/Book";

const Books = () => {
  const [books, setBooks] = useState([]);

  useEffect(() => {
    fetchBooks();
  }, []);

  const fetchBooks = async () => {
    try {
      const response = await api.get("/books");
      setBooks(response.data);
    } catch {}
  };

  return (
    <div className="books-container main-container">
        <h1 className="text-primary">Books</h1>
        <div className="d-flex flex-wrap justify-content-around">
            {books.map((book)=>(
                <Book key={book.id} book={book}/>
            ))}
        </div>
    </div>
  );
}

export default Books;