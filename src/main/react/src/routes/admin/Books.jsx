import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { Link } from "react-router-dom";

import Loading from "@/components/Loading";
import AdminBook from "@/Components/AdminBook";
import AdminBookForm from "@/Components/AdminBookForm";

const Books = () => {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [genres, setGenres] = useState([]);
  const [editingBook, setEditingBook] = useState(null);
  const [newBook, setNewBook] = useState(false);

  useEffect(() => {
    fetchBooks();
    fetchGenres();
  }, []);

  const fetchBooks = async () => {
    setLoading(true);
    try {
      const response = await api.get("/admin/books");
      setBooks(response.data);
    } catch {}
    setLoading(false);
  };

  const fetchGenres = async () => {
    try {
      const response = await api.get("/books/genres");
      setGenres(response.data);
    } catch {}
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <div className="admin-books-container main-container">
      <Link to="/admin">Back</Link>
      <h1 className="text-primary">Books</h1>
      <button className="btn btn-success mb-3" onClick={() => setNewBook(true)}>New Book</button>

      {newBook && <AdminBookForm book={null} genres={genres} onSave={fetchBooks} onCancel={() => setNewBook(false)} />}

      <div className="table-responsive">
        <table className="table">
          <thead>
            <tr>
              <th className="col-1">#</th>
              <th className="col-1">Title</th>
              <th className="col-1">Author</th>
              <th className="col-1">Genre</th>
              <th className="col-1">Description</th>
              <th className="col-1">Online</th>
              <th className="col-1">Copies</th>
              <th className="col-1">Price</th>
              <th className="col-1">Actions</th>
            </tr>
          </thead>
          <tbody>
            {books.map((book) => (
              <AdminBook key={book.id} book={book} genres={genres} editingBook={editingBook}
                setEditingBook={setEditingBook} onSave={fetchBooks} />
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Books;
