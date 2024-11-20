import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import * as bootstrap from 'bootstrap'
import './scss/styles.scss'

import Navbar from "./components/Navbar"
import Home from './routes/Home'
import Login from "./routes/Login";
import Register from "./routes/Register";
import Profile from "./routes/Profile";
import ProfileUpdate from "./routes/ProfileUpdate";
import ProfileUpdatePassword from "./routes/ProfileUpdatePassword";
import PasswordReset from "./routes/PasswordReset";
import PasswordResetSubmit from "./routes/PasswordResetSubmit";

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <Navbar />
      <div>
        <Routes>
          <Route path="/" element={<Home />} />

          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          <Route path="/profile" element={<Profile />} />
          <Route path="/profile/update" element={<ProfileUpdate />} />
          <Route path="/profile/update-password" element={<ProfileUpdatePassword />} />

          <Route path="/password" element={<PasswordReset />} />
          <Route path="/password/reset" element={<PasswordResetSubmit />} />
        </Routes>
      </div>
    </Router>
  </React.StrictMode>,
)