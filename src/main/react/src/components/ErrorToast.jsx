import React from 'react';
import { useError } from '@/context/ErrorContext';

const ErrorToast = () => {
  const { error, clearError } = useError();

  if (!error) return null;

  return (
    <div className="error-toast bg-light row">
      <div className="justify-content-middle col text">
      <h5 className="text-black text-bold">{error.status}</h5>
      <p className="text-black">{error.message}</p>
      </div>
      <button className="btn bg-light text-dark text-align-end col-1" onClick={clearError}><img src="/close.png" alt="Close" /></button>
    </div>
  );
};

export default ErrorToast;
