import React, { useEffect, useState } from "react";
import api from "~/axios.config";
import { Link } from "react-router-dom";
import Loading from "@/components/Loading";

const History = () => {
  const [orders, setOrders] = useState([]);
  const [purchases, setPurchases] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchHistoryData = async () => {
      const [ordersResponse, purchasesResponse] = await Promise.all([
        api.get("/orders"),
        api.get("/purchases"),
      ]);
      setOrders(ordersResponse.data);
      setPurchases(purchasesResponse.data);
      setLoading(false);
    };

    fetchHistoryData();
  }, []);

  const renderOrders = () => {
    return orders.map((order, index) => (
      <div key={index}>
        <p>Date: {order.date}</p>
      </div>
    ));
  };

  const renderPurchases = () => {
    return purchases.map((purchase, index) => (
      <div key={index}>
        <p>Date: {purchase.date}</p>
        <p>Price: ${purchase.price}</p>
      </div>
    ));
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <div className="history-container main-container">
      <Link to="/user">Back</Link><h1 className="text-primary">History</h1>

      <div className="orders-section">
        <h2 className="text-secondary">Orders</h2>
        {orders.length > 0 ? renderOrders() : <p>No orders found.</p>}
      </div>

      <div className="purchases-section">
        <h2 className="text-secondary">Purchases</h2>
        {purchases.length > 0 ? renderPurchases() : <p>No purchases found.</p>}
      </div>
    </div>
  );
}

export default History;
