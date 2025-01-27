import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import Book from "@/components/Book";

const Books = () => {
  const [books, setBooks] = useState([]);
  const [genres, setGenres] = useState([]);
  const [selectedGenres, setSelectedGenres] = useState([]);
  const [sortOption, setSortOption] = useState("name");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalBooks, setTotalBooks] = useState(0);
  const itemsPerPage = 10;

  useEffect(() => {
    fetchGenres();
    fetchBooks();
  }, [selectedGenres, sortOption, currentPage]);

  const fetchBooks = async () => {
    try {
      const response = await api.get("/books/filtered", {
        params: {
          genres: selectedGenres.join(","),
          sort: sortOption,
          page: currentPage,
          size: itemsPerPage,
        },
      });
      setBooks(response.data.books);
      setTotalBooks(response.data.total);
    } catch (error) {}
  };

  const fetchGenres = async () => {
    try {
      const response = await api.get("/books/genres");
      setGenres(response.data);
    } catch (error) {}
  };

  const handleGenreChange = (genre) => {
    if (selectedGenres.includes(genre)) {
      setSelectedGenres(selectedGenres.filter((g) => g !== genre));
    } else {
      setSelectedGenres([...selectedGenres, genre]);
    }
    setCurrentPage(1);
  };

  return (
    <div className="books-container main-container">
      <h1 className="text-primary">Books</h1>
      <div className="genres-filter mb-3">
        <h5>Filter by Genres</h5>
        <div className="d-flex flex-wrap gap-2 justify-content-start align-items-start">
            {genres.map((g) => (
              <div key={g.genre} className="form-check">
                <input
                  className="form-check-input"
                  type="checkbox"
                  id={g.genre}
                  value={g.name}
                  checked={selectedGenres.includes(g.genre)}
                  onChange={() => handleGenreChange(g.genre)}
                />
                <label className="form-check-label" htmlFor={g.genre}>
                  {g.name}
                </label>
              </div>
            ))}
        </div>
      </div>
      <div className="sort-options mb-3">
        <h5>Sort by</h5>
        <div className="btn-group d-flex flex-wrap rounded-0" role="group" aria-label="Sort Options">
          <button
            className={`btn rounded-0 ${
              sortOption === "name" ? "btn-primary" : "btn-outline-primary"
            }`}
            onClick={() => setSortOption("name")}
          >Name</button>
          <button
            className={`btn rounded-0 ${
              sortOption === "price-asc" ? "btn-primary" : "btn-outline-primary"
            }`}
            onClick={() => setSortOption("price-asc")}
          >Price-low to high</button>
          <button
            className={`btn rounded-0 ${
              sortOption === "price-desc" ? "btn-primary" : "btn-outline-primary"
            }`}
            onClick={() => setSortOption("price-desc")}
          >Price-high to low</button>
        </div>
      </div>
      <div className="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 row-cols-xl-5 g-4">
        {books.map((book) => (
          <Book key={book.id} book={book} />
        ))}
      </div>
      <div className="pagination mt-4 d-flex flex-row justify-content-center align-items-center gap-2">
        {Array.from(
          { length: Math.ceil(totalBooks / itemsPerPage) },
          (_, index) => (
            <button
              key={index}
              onClick={() => setCurrentPage(index + 1)}
              className={`btn ${
                currentPage === index + 1 ? "btn-primary" : "btn-outline-primary"
              }`}
            >{index + 1}</button>
          )
        )}
      </div>
    </div>
  );
};

export default Books;