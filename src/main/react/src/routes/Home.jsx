import React, { useState, useEffect } from 'react';
import { Link } from "react-router-dom";
import api from "~/axios.config";
import AsyncSelect from 'react-select/async';

import Book from "@/components/Book";
import Banner from "@/components/Banner";

const Home = () => {
  const [books, setBooks] = useState([]);
  const [bestBooks, setBestBooks] = useState([]);

  useEffect(() => {
    fetchBooks();
    fetchBestBooks();
  }, []);

  const fetchBooks = async () => {
    try {
      const response = await api.get("/books");
      setBooks(response.data);
      console.log(books);
    } catch {}
  };
  
  const fetchBestBooks = async () => {
      try {
        const response = await api.get("/books/best?limit=4");
        setBestBooks(response.data);
        console.log(books);
      } catch {}
    };

  const filterBooks = (inputValue) => {
    return books.filter((b) =>
      b.title.toLowerCase().includes(inputValue.toLowerCase())
    ).slice(0,7);
  };

  const loadOptions = (inputValue, callback) => {
    setTimeout(() => {
      callback(filterBooks(inputValue));
    }, 1000);
  };

  const selectStyles = {
    control: styles => ({ ...styles, margin: 0, padding: 0, }),
    option: (styles, { data, isDisabled, isFocused, isSelected }) => {
      return {
        ...styles,
        margin: 0,
        padding: 0,
      };
    },
  };

  const formatOptionLabel = ({ id, title, author }) => (
      <Link className="text-decoration-none text-dark w-100" to={`/books/${id}`}>
          <div className="search-option py-2 px-3">
            <p className="text-dark m-0 fw-bold">{title}</p>
            <p className="text-muted m-0">{author}</p>
          </div>
      </Link>
    );

  return (
    <>
      <header className="home-header">
        <div className="home-header-overshadow p-4 d-flex flex-column justify-content-center align-items-center">
          <h1 className="text-white text-uppercase">Book Booking</h1>
          <p className="text-light text-center">The best online-library you will ever find. Reserve or buy, look
              for whatever you want, we have everything!</p>
          <AsyncSelect
            className="home-header-search"
            cacheOptions
            loadOptions={loadOptions}
            defaultOptions
            styles={selectStyles}
            isSelectable={false}
            isClearable={true}
            isSearchable={true}
            formatOptionLabel={formatOptionLabel}
            placeholder="Find your book..."
          />
        </div>
      </header>
      <div className="home-container main-container d-flex flex-column justify-content-center align-items-center">
          <h2 className="text-uppercase my-4 text-start fs-1">Welcome in our library</h2>
          <div className="home-container-text">
            <p>
              Vítejte v naší online knihovně, místě, kde se literatura setkává s moderní technologií. Nabízíme
              širokou škálu knih všech žánrů, od napínavých thrillerů přes inspirativní biografie až po nadčasovou
              klasiku. Naše platforma je navržena tak, aby vám poskytla jednoduchý a pohodlný přístup k oblíbeným
              knihám, ať už jste kdekoli. Ať už hledáte odpočinek s dobrou knihou, nebo se chcete ponořit do světa
              nových poznatků, naše knihovna je tu pro vás.
            </p>
            <p>
              Naše mise je podporovat čtení a vzdělávání prostřednictvím inovativního a snadno
              použitelného prostředí. Díky uživatelsky přívětivému vyhledávání, personalizovaným doporučením
              a recenzím od ostatních čtenářů si u nás každý najde něco pro sebe. Připojte se k naší komunitě
              nadšenců do knih a objevte, jak snadné je mít svou vlastní knihovnu na dosah ruky.
            </p>
          </div>
          <h2 className="text-uppercase my-4 text-start fs-1">Our best choices</h2>
          <div className="home-container-best-choice row row-cols-1 row-cols-sm-2 row-cols-lg-4 g-4">
            {bestBooks.map((book)=>(
              <Book key={book.id} book={book}/>
            ))}
          </div>
      </div>
      <Banner />
    </>
  )
}

export default Home
