import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import { setupAxiosInterceptors } from '~/axios.config';
import { ErrorProvider, useError } from '@/context/ErrorContext';

import Navbar from "@/components/Navbar";
import ErrorToast from "@/components/ErrorToast";
import Home from '@/routes/Home';
import Login from "@/routes/Login";
import Register from "@/routes/Register";
import Profile from "@/routes/Profile";
import ProfileUpdate from "@/routes/ProfileUpdate";
import ProfileUpdatePassword from "@/routes/ProfileUpdatePassword";
import PasswordReset from "@/routes/PasswordReset";
import PasswordResetSubmit from "@/routes/PasswordResetSubmit";

import * as bootstrap from 'bootstrap';
import './scss/styles.scss';

const App = () => {
  const { addError } = useError();

  React.useEffect(() => {
    setupAxiosInterceptors(addError);
  }, [addError]);

  return (
    <>
      <ErrorToast />
      <Navbar />
      <div>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/user" element={<Profile />} />
          <Route path="/user/update" element={<ProfileUpdate />} />
          <Route path="/user/update-password" element={<ProfileUpdatePassword />} />
          <Route path="/password" element={<PasswordReset />} />
          <Route path="/password/reset" element={<PasswordResetSubmit />} />
        </Routes>
      </div>
    </>
  );
};

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ErrorProvider>
      <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
        <App />
      </Router>
    </ErrorProvider>
  </React.StrictMode>
);