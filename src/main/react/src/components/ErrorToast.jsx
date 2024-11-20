import React from 'react';
import { useError } from '@/context/ErrorContext';

const ErrorToast = () => {
  const { error, clearError } = useError();

  if (!error) return null;

  return (
    <div className="error-toast bg-danger">
      <p className="text-light">{error.message}</p>
      <button className="btn btn-danger" onClick={clearError}>Close</button>
    </div>
  );
};

export default ErrorToast;
