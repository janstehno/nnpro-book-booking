import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { useNavigate } from "react-router-dom";
import CartItemType from "@/utils/CartItemType";

const Cart = () => {
  const [cart, setCart] = useState(JSON.parse(localStorage.getItem("booking-cart")) || []);
  const navigate = useNavigate();

  const bookingItems = cart.filter(item => item.type === CartItemType.BOOKING);
  const purchaseItems = cart.filter(item => item.type === CartItemType.PURCHASE);

  useEffect(() => {
    localStorage.setItem("booking-cart", JSON.stringify(cart));
  }, [cart]);

  const handleQuantityChange = (itemId, increment) => {
    let updatedCart = [...cart];
    const itemIndex = updatedCart.findIndex(item => item.id === itemId);
    if (itemIndex !== -1) {
      let updatedQuantity = updatedCart[itemIndex].quantity + increment;
      if (updatedQuantity <= updatedCart[itemIndex].physicalCopies && updatedQuantity >= 0) {
        updatedCart[itemIndex].quantity = updatedQuantity;
      }
      if (updatedQuantity === 0) {
        updatedCart.splice(itemIndex, 1);
      }
    setCart(updatedCart);
    }
  };

  const renderCartItems = (items) => {
    return items.map(item => (
      <tr key={item.id}>
        <td>{item.title}</td>
        { item.type === CartItemType.BOOKING && <td>
          <button className="btn btn-light minus" onClick={() => handleQuantityChange(item.id, -1)} disabled={item.quantity <= 0}>
          <img src="/minus.png" alt="Minus" />
          </button>
          {item.quantity}
          <button className="btn btn-light plus" onClick={() => handleQuantityChange(item.id, 1)} disabled={item.quantity >= item.physicalCopies}>
          <img src="/plus.png" alt="Plus" />
          </button>
        </td> }
        { item.type === CartItemType.PURCHASE && <td>{item.ebookPrice}</td> }
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
        const data = transformData(items);
        await api.post(url, data);
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
                <th className="col-1">Quantity</th>
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
