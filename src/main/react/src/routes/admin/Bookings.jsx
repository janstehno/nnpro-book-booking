import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { Link, useNavigate, useParams } from "react-router-dom";
import Loading from "@/components/Loading";

const UserBookings = () => {
  const { userId } = useParams();
  const [bookings, setBookings] = useState(null);
  const [returnIds, setReturnIds] = useState([]);
  const [loanIds, setLoanIds] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/admin/users/${userId}/bookings`);
      setBookings(response.data);
    } catch {}
    setLoading(false);
  };

  const handleReturnChange = (bookingId, isChecked) => {
    setReturnIds((prev) => {
      if (isChecked) {
        return [...prev, bookingId];
      } else {
        return prev.filter((id) => id !== bookingId);
      }
    });
  };

  const handleLoanChange = (bookingId, isChecked) => {
    setLoanIds((prev) => {
      if (isChecked) {
        return [...prev, bookingId];
      } else {
        return prev.filter((id) => id !== bookingId);
      }
    });
  };

  const submitReturnIds = async () => {
    try {
      const response = await api.put(`/admin/users/${userId}/bookings`,
          { "returnIds": returnIds, "loanIds": loanIds });
      setBookings(response.data);
    } catch {}
  };

  if (loading) {
    return <Loading />;
  }

  return (
    bookings && (
      <div className="admin-bookings-container main-container">
        <Link to="/admin/users">Back</Link>
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
                booking.status === "CANCELED" ||
                booking.status === "ONLINE";

              const isLockedForLoan =
                booking.status === "WAITING" ||
                booking.status === "LOANED" ||
                booking.status === "RETURNED" ||
                booking.status === "UNCLAIMED" ||
                booking.status === "CANCELED" ||
                booking.status === "ONLINE";

              return (
                <tr key={booking.id}>
                  <td>
                    <input
                      type="checkbox"
                      disabled={isLockedForReturn}
                      checked={returnIds.includes(booking.id)}
                      onChange={(e) =>
                        handleReturnChange(
                          booking.id,
                          e.target.checked && !e.target.disabled
                        )
                      }
                    />
                  </td>
                  <td>
                    <input
                      type="checkbox"
                      disabled={isLockedForLoan}
                      checked={loanIds.includes(booking.id)}
                      onChange={(e) =>
                        handleLoanChange(
                          booking.id,
                          e.target.checked && !e.target.disabled
                        )
                      }
                    />
                  </td>
                  <td>{booking.book.title}</td>
                  <td>{booking.count === 0 ? null : booking.count}</td>
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
          disabled={returnIds.length === 0 && loanIds.length === 0}
        >
          Update Bookings
        </button>
      </div>
    )
  );
};

export default UserBookings;
