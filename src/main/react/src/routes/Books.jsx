import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import Book from "@/components/Book";

function Books() {
  const [books, setBooks] = useState([]);

  useEffect(() => {
    const fetchBooks = async () => {
      try {
        const response = await api.get("/books");
        setBooks(response.data);
      } catch (error) {
        console.error("Failed to get books", error);
      }
    };
    fetchBooks();
  }, []);

  return (
    <div className="books-container main-container">
        <h1 className="text-primary">Books</h1>
        <div className="d-flex flex-wrap justify-content-between">
            {books.map((book)=>(
                <Book key={book.id} book={book}/>
            ))}
        </div>
    </div>
  );
}

export default Books;