import React from "react";
import api from "~/axios.config";
import { useNavigate } from "react-router-dom";
import CartItemType from "@/utils/CartItemType";

function Cart() {
  const navigate = useNavigate();

  const cart = JSON.parse(localStorage.getItem("booking-cart")) || [];
  const bookingItems = cart.filter(item => item.type === CartItemType.BOOKING);
  const purchaseItems = cart.filter(item => item.type === CartItemType.PURCHASE);

  const renderCartItems = (items) => {
    return items.map(item => (
      <tr key={item.id}>
        <td>{item.title}</td>
        <td>{item.quantity}</td>
        {item.type === CartItemType.PURCHASE && <td>${item.ebookPrice}</td>}
      </tr>
    ));
  };

  const handleSubmitAll = async () => {
    const endpoints = [
      {
        type: "booking",
        items: bookingItems,
        url: "/orders/new",
        transformData: (items) => ({
          books: items.reduce((map, item) => {
            map[item.id] = item.quantity;
            return map;
          }, {})
        }),
      },
      {
        type: "purchase",
        items: purchaseItems,
        url: "/purchases/new",
        transformData: (items) => ({
          bookIds: items.map(item => item.id),
        }),
      },
    ];

    for (const { type, items, url, transformData } of endpoints) {
      if (items.length > 0) {
        try {
          const data = transformData(items);
          await api.post(url, data);
        } catch (error) {
          console.error(`Error submitting ${type}:`, error);
          return;
        }
      }
    }

    localStorage.removeItem("booking-cart");
    navigate("/user/history");
  };

  const hasItemsInCart = bookingItems.length > 0 || purchaseItems.length > 0;

  return (
    <div className="cart-container main-container">
      <h1 className="text-primary">Cart</h1>
      <div>
        <h2 className="text-secondary">Booking</h2>
        {bookingItems.length > 0 ? (
          <table className="table">
            <thead>
              <tr>
                <th className="col-3">Title</th>
                <th className="col-2">Quantity</th>
              </tr>
            </thead>
            <tbody>
              {renderCartItems(bookingItems)}
            </tbody>
          </table>
        ) : (
          <p>No bookings in the cart.</p>
        )}
      </div>
      <div>
        <h2 className="text-secondary">Purchase</h2>
        {purchaseItems.length > 0 ? (
          <table className="table">
            <thead>
              <tr>
                <th className="col-3">Title</th>
                <th className="col-1">Quantity</th>
                <th className="col-1">Price</th>
              </tr>
            </thead>
            <tbody>
              {renderCartItems(purchaseItems)}
            </tbody>
          </table>
        ) : (
          <p>No purchases in the cart.</p>
        )}
      </div>

      {hasItemsInCart && (
        <div className="proceed-button">
          <button className="btn btn-warning" onClick={handleSubmitAll}>
            Proceed
          </button>
        </div>
      )}
    </div>
  );
}

export default Cart;
