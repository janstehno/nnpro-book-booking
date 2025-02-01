import React, { useState } from "react";

import AdminBookForm from "./AdminBookForm";

const AdminBook = ({ book, genres, editingBook, setEditingBook, onSave }) => {
  return (
    <>
      {editingBook === book.id ? (
        <AdminBookForm book={book} genres={genres} onSave={() => { setEditingBook(null); onSave(); }} onCancel={() => setEditingBook(null)} />
      ) : (
        <tr>
          <td>{book.id}</td>
          <td>{book.title}</td>
          <td>{book.author}</td>
          <td>{book.genre}</td>
          <td>{book.description}</td>
          <td>{book.online ? "Yes" : "No"}</td>
          <td>{book.physicalCopies}</td>
          <td>{book.ebookPrice > 0 ? `$${book.ebookPrice}` : "-"}</td>
          <td>
            <button className="btn btn-primary btn-sm" onClick={() => setEditingBook(book.id)}>Edit</button>
          </td>
        </tr>
      )}
    </>
  );
};

export default AdminBook;
