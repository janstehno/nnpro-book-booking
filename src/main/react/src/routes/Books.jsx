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
        <div className="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 row-cols-xl-5 g-4">
            {books.map((book)=>(
                <Book key={book.id} book={book}/>
            ))}
        </div>
    </div>
  );
}

export default Books;