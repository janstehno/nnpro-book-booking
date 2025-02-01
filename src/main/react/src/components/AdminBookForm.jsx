import React, { useState } from "react";
import api from "~/axios.config";

const AdminBookForm = ({ book, genres, onSave, onCancel }) => {
  const [formData, setFormData] = useState(book || {
    title: "", author: "", genre: "", description: "",
    online: false, physicalCopies: "", ebookPrice: ""
  });

  const handleChange = (e, field) => {
    const value = field === "online" ? e.target.checked : e.target.value;
    setFormData({ ...formData, [field]: value });
  };

  const handleSubmit = async () => {
    try {
      if (book) {
        await api.put("/admin/books", formData);
      } else {
        await api.post("/admin/books", formData);
      }
      onSave();
    } catch {}
  };

  return (
    <tr>
      <td colspan="9" className="p-4">
        <form className="form form-admin-book">
            <div>
                <label for="form-title" className="form-label">Title</label>
                <input className="form-control w-100" id="form-title" type="text" value={formData.title} onChange={(e) => handleChange(e, "title")} />
            </div>
            <div>
                <label for="form-author" className="form-label">Author</label>
                <input className="form-control w-100" id="form-author" type="text" value={formData.author} onChange={(e) => handleChange(e, "author")} />
            </div>
            <div>
                <label for="form-genre" className="form-label">Genre</label>
                <select className="form-control w-100" id="form-genre" value={formData.genre} onChange={(e) => handleChange(e, "genre")}>
                  {genres.map((genre) => (
                    <option key={genre.genre} value={genre.genre}>{genre.genre}</option>
                  ))}
                </select>
            </div>
            <div>
                <label for="form-description" className="form-label">Description</label>
                <textarea className="form-control w-100" id="form-description" value={formData.description} onChange={(e) => handleChange(e, "description")} />
            </div>
            <div>
                <label for="form-online" className="form-label me-2">Online</label>
                <input type="checkbox" id="form-online" checked={formData.online} onChange={(e) => handleChange(e, "online")} />
            </div>
            <div>
                <label for="form-physical-copies" className="form-label">Physical copies</label>
                <input className="form-control" id="form-physical-copies" type="number" value={formData.physicalCopies} onChange={(e) => handleChange(e, "physicalCopies")} />
            </div>
            <div>
                <label for="form-ebook-price" className="form-label">E-book Price</label>
                <input className="form-control" id="form-ebook-price" type="number" value={formData.ebookPrice} onChange={(e) => handleChange(e, "ebookPrice")} />
            </div>
            <div className="d-flex flex-row gap-1">
                <button className="btn btn-success btn-sm me-1" onClick={handleSubmit}>Save</button>
                <button className="btn btn-secondary btn-sm" onClick={onCancel}>Cancel</button>
            </div>
        </form>
      </td>
    </tr>
  );
};

export default AdminBookForm;
