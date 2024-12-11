import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { useNavigate, useParams } from "react-router-dom";
import Loading from "@/components/Loading";

const Bookings = () => {
  const { userId } = useParams();
  const [bookings, setBookings] = useState(null);
  const [returningIds, setReturningIds] = useState([]);
  const [loaningIds, setLoaningIds] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/admin/bookings/${userId}`);
      setBookings(response.data);
    } catch {}
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
    try {
      const response = await api.put(`/admin/bookings/${userId}`,
          { "returningBookIds": returningIds, "loaningBookIds": loaningIds });
      setBookings(response.data);
    } catch {}
  };

  if (loading) {
    return <Loading />;
  }

  return (
    bookings && (
      <div className="admin-bookings-container main-container">
        <h1 className="text-primary">User Bookings</h1>
        <table className="table table-hover">
          <thead>
            <tr>
              <th scope="col">Return</th>
              <th scope="col">Loan</th>
              <th scope="col">Title</th>
              <th scope="col">Count</th>
              <th scope="col">Booking Date</th>
              <th scope="col">Expiration Date</th>
              <th scope="col">Status</th>
            </tr>
          </thead>
          <tbody>
            {bookings.map((booking) => {
              const isLockedForReturn =
                booking.status === "WAITING" ||
                booking.status === "AVAILABLE" ||
                booking.status === "RETURNED" ||
                booking.status === "UNCLAIMED" ||
                booking.status === "CANCELED";

              const isLockedForLoan =
                booking.status === "WAITING" ||
                booking.status === "LOANED" ||
                booking.status === "RETURNED" ||
                booking.status === "UNCLAIMED" ||
                booking.status === "CANCELED";

              return (
                <tr key={booking.book.id}>
                  <td>
                    <input
                      type="checkbox"
                      disabled={isLockedForReturn}
                      checked={returningIds.includes(booking.book.id)}
                      onChange={(e) =>
                        handleReturnChange(
                          booking.book.id,
                          e.target.checked && !e.target.disabled
                        )
                      }
                    />
                  </td>
                  <td>
                    <input
                      type="checkbox"
                      disabled={isLockedForLoan}
                      checked={loaningIds.includes(booking.book.id)}
                      onChange={(e) =>
                        handleLoanChange(
                          booking.book.id,
                          e.target.checked && !e.target.disabled
                        )
                      }
                    />
                  </td>
                  <td>{booking.book.title}</td>
                  <td>{booking.count}</td>
                  <td>{new Date(booking.bookingDate).toLocaleDateString()}</td>
                  <td>{new Date(booking.expirationDate).toLocaleDateString()}</td>
                  <td>
                    <span className={`status ${booking.status.toLowerCase()}`}>{booking.status.toLowerCase()}</span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        <button
          className="btn btn-primary"
          onClick={submitReturnIds}
          disabled={returningIds.length === 0 && loaningIds.length === 0}
        >
          Update Bookings
        </button>
      </div>
    )
  );
};

export default Bookings;
