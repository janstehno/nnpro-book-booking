import React, { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const ErrorContext = createContext();

export const ErrorProvider = ({ children }) => {
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const clearError = () => {
    setError(null);
  };

  const addError = (e) => {
    setError(e);
    setTimeout(() => {
      clearError();
    }, 3000);
  };

  return (
    <ErrorContext.Provider value={{ error, addError, clearError }}>
      {children}
    </ErrorContext.Provider>
  );
};

export const useError = () => useContext(ErrorContext);
