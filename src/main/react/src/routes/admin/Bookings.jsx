import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { useNavigate } from "react-router-dom";
import Loading from "@/components/Loading";

const Bookings = () => {
  const [bookings, setBookings] = useState(null);
  const [returningIds, setReturningIds] = useState([]);
  const [loaningIds, setLoaningIds] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchBookings();
  }, []);

  useEffect(() => {
      console.log(returningIds);
  }, [returningIds]);

  const fetchBookings = async () => {
    setLoading(true);
    const response = await api.get("/admin/bookings/1");
    console.log(response.data);
    setBookings(response.data);
    setLoading(false);
  };

  const handleReturnChange = (bookId, isChecked) => {
      setReturningIds((prev) => {
          if (isChecked) {
              return [...prev, bookId];
          } else {
              return prev.filter((id) => id !== bookId);
          }
      });
  };

  const handleLoanChange = (bookId, isChecked) => {
      setLoaningIds((prev) => {
          if (isChecked) {
              return [...prev, bookId];
          } else {
              return prev.filter((id) => id !== bookId);
          }
      });
  };

  const submitReturnIds = async () => {
    const response = await api.put("/admin/bookings/1", returningIds);
    fetchBookings();
  };

  if (loading) {
      return <Loading />;
  }

  return bookings && (
    <div className="admin-bookings-container main-container">
      <h1 className="text-primary">User Bookings</h1>
      <ul>
        {bookings.map((booking) => {
          const isLockedForReturn =
            booking.status === "WAITING" ||
            booking.status === "AVAILABLE" ||
            booking.status === "RETURNED" ||
            booking.status === "UNCLAIMED" ||
            booking.status === "CANCELED";            
            
          const isLockedForLoan =
            booking.status === "WAITING" ||
            booking.status === "RETURNED" ||
            booking.status === "UNCLAIMED" ||
            booking.status === "CANCELED";

          return (
            <li key={booking.book.id} className="d-flex flex-row justify-content-between align-items-center">
              <label className="col-1">
                <input
                  type="checkbox"
                  disabled={isLockedForReturn}
                  checked={returningIds.includes(booking.book.id)}
                  onChange={(e) => handleReturnChange(booking.book.id, e.target.checked && !e.target.disabled)}
                />
              </label>
              <label className="col-1">
                <input
                  type="checkbox"
                  disabled={isLockedForLoan}
                  checked={loaningIds.includes(booking.book.id)}
                  onChange={(e) => handleLoanChange(booking.book.id, e.target.checked && !e.target.disabled)}
                />
              </label>
                <p className="col">{booking.book.title}</p>
                <p className="col">{booking.count}</p>
                <p className="col">{booking.expirationDate}</p>
                <p className={`col-1 status ${booking.status.toLowerCase()}`}>{booking.status.toLowerCase()}</p>
            </li>
          );
        })}
      </ul>
      <button className="btn btn-primary" onClick={submitReturnIds} disabled={returningIds.length === 0}>
        Update Bookings
      </button>
    </div>
  );
};

export default Bookings;
