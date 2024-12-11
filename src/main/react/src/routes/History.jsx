import React, { useEffect, useState } from "react";
import api from "~/axios.config";
import { Link, useNavigate } from "react-router-dom";
import Loading from "@/components/Loading";

const History = () => {
  const [orders, setOrders] = useState([]);
  const [purchases, setPurchases] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchHistoryData();
  }, []);

  const fetchHistoryData = async () => {
    setLoading(true);
    const ordersResponse = await api.get("/orders");
    const purchasesResponse = await api.get("/purchases");
    setOrders(ordersResponse.data);
    setPurchases(purchasesResponse.data);
    setLoading(false);
  };

  const handleOrderDetailsClick = (orderId) => {
    navigate(`/orders/${orderId}`);
  };

  const handlePurchaseDetailsClick = (purchaseId) => {
    navigate(`/purchases/${purchaseId}`);
  };

  if (loading) {
    return <Loading />;
  }

  return orders && purchases && (
    <div className="history-container main-container">
      <Link to="/user">Back</Link>
      <h1 className="text-primary">History</h1>

      <div className="orders-section">
        <h2 className="text-secondary">Orders</h2>
        {orders.length > 0 ? (
          <table className="table table-hover justify-content-between align-center">
            <thead>
              <tr>
                <th scope="col">Date</th>
                <th scope="col">Details</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order, index) => (
                <tr key={index}>
                  <td>{new Date(order.date).toLocaleDateString()}</td>
                  <td>
                    <button
                      className="btn btn-primary btn-sm ms-auto"
                      onClick={() => handleOrderDetailsClick(order.id)}
                    >
                      Details
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>No orders found.</p>
        )}
      </div>

      <div className="purchases-section">
        <h2 className="text-secondary">Purchases</h2>
        {purchases.length > 0 ? (
          <table className="table table-hover">
            <thead>
              <tr>
                <th scope="col">Date</th>
                <th scope="col">Price</th>
                <th scope="col">Details</th>
              </tr>
            </thead>
            <tbody>
              {purchases.map((purchase, index) => (
                <tr key={index}>
                  <td>{new Date(purchase.date).toLocaleDateString()}</td>
                  <td>${purchase.price}</td>
                  <td>
                    <button
                      className="btn btn-primary btn-sm ms-auto"
                      onClick={() => handlePurchaseDetailsClick(purchase.id)}
                    >
                      Details
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>No purchases found.</p>
        )}
      </div>

    </div>
  );
};

export default History;
