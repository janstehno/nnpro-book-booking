import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { Link, useNavigate, useParams } from "react-router-dom";
import Loading from "@/components/Loading";

const Order = () => {
  const { orderId } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchOrder();
  }, []);

  const fetchOrder = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/orders/${orderId}`);
      setOrder(response.data);
    } catch {}
    setLoading(false);
  };

  const handleCancel = async (bookingId) => {
    try {
      const response = await api.post(`/orders/${orderId}/bookings/${bookingId}/cancel`);
      fetchOrder();
    } catch {}
  }

  if (loading) {
    return <Loading />;
  }

  return (
    order && (
      <div className="order-container main-container">
        <Link to="/user/history">Back</Link>
        <h1 className="text-primary">Order Details</h1>
        <table className="table table-hover">
          <thead>
            <tr>
              <th scope="col">Title</th>
              <th scope="col">Count</th>
              <th scope="col">Booking Date</th>
              <th scope="col">Expiration Date</th>
              <th scope="col">Status</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            {order.bookings.map((booking) => {
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
                <tr key={booking.book.id}>
                  <td>{booking.book.title}</td>
                  <td>{booking.count === 0 ? null : booking.count}</td>
                  <td>{new Date(booking.bookingDate).toLocaleDateString()}</td>
                  <td>{booking.expirationDate ? new Date(booking.expirationDate).toLocaleDateString() : ''}</td>
                  <td>
                    <span className={`status ${booking.status.toLowerCase()}`}>{booking.status.toLowerCase()}</span>
                  </td>
                  <td>
                    {(booking.status === "WAITING" || booking.status === "AVAILABLE") ? (
                      <button className="btn btn-danger text-light status" onClick={() => handleCancel(booking.id)}>Cancel</button>
                    ) : booking.status === "ONLINE" ? (
                      <button className="btn btn-light text-dark status">Show Online</button>
                    ) : ( <></> )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    )
  );
};

export default Order;
