import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { setupAxiosInterceptors } from '~/axios.config';

import { ErrorProvider, useError } from '@/context/ErrorContext';
import ErrorToast from "@/components/ErrorToast";

import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";

import Home from '@/routes/Home';
import Login from "@/routes/Login";
import Register from "@/routes/Register";
import Profile from "@/routes/Profile";
import ProfileUpdate from "@/routes/ProfileUpdate";
import ProfileUpdatePassword from "@/routes/ProfileUpdatePassword";
import History from "@/routes/History";
import Order from "@/routes/Order";
import Purchase from "@/routes/Purchase";
import PasswordReset from "@/routes/PasswordReset";
import PasswordResetSubmit from "@/routes/PasswordResetSubmit";
import Books from "@/routes/Books";
import BookDetail from "@/routes/BookDetail";
import Cart from "@/routes/Cart";

import AdminHome from "@/routes/admin/Home";
import AdminUsers from "@/routes/admin/Users";
import AdminBookings from "@/routes/admin/Bookings";

import * as bootstrap from 'bootstrap';
import './scss/styles.scss';

const App = () => {
  const { error, addError } = useError();

  React.useEffect(() => {
    setupAxiosInterceptors(addError);
  }, [addError]);

  return (
    <>
      <Navbar />
      {error && <ErrorToast />}
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/user" element={<Profile />} />
        <Route path="/user/update" element={<ProfileUpdate />} />
        <Route path="/user/update-password" element={<ProfileUpdatePassword />} />
        <Route path="/user/history" element={<History />} />
        <Route path="/orders/:orderId" element={<Order />} />
        <Route path="/purchases/:purchaseId" element={<Purchase />} />
        <Route path="/password" element={<PasswordReset />} />
        <Route path="/password/reset" element={<PasswordResetSubmit />} />
        <Route path="/books" element={<Books />} />
        <Route path="/books/:bookId" element={<BookDetail />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/admin" element={<AdminHome />} />
        <Route path="/admin/users" element={<AdminUsers />} />
        <Route path="/admin/users/:userId/bookings" element={<AdminBookings />} />
      </Routes>
      <Footer />
    </>
  );
};

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <ErrorProvider>
        <App />
      </ErrorProvider>
    </Router>
  </React.StrictMode>
);
