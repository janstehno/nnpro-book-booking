import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { useNavigate } from "react-router-dom";

import CartItemType from "@/utils/CartItemType";
import CartItems from "@/components/CartItems";

const Cart = () => {
  const [cart, setCart] = useState(JSON.parse(localStorage.getItem("booking-cart")) || []);
  const navigate = useNavigate();

  const bookingItems = cart.filter(item => item.type === CartItemType.BOOKING || item.type === CartItemType.ONLINE);
  const purchaseItems = cart.filter(item => item.type === CartItemType.PURCHASE);

  useEffect(() => {
    localStorage.setItem("booking-cart", JSON.stringify(cart));
  }, [cart]);

  const handleQuantityChange = (changedItem, increment) => {
      let updatedCart = [...cart];
      const itemIndex = updatedCart.findIndex(item => item.id === changedItem.id && item.type === changedItem.type && item.online === changedItem.online);
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

  const handleOnlineToggle = (changedItem) => {
    const updatedCart = cart.filter((item) => {
      if (item.id === changedItem.id && item.type === changedItem.type && item.type == CartItemType.ONLINE && item.online === changedItem.online && item.online) {
        return false;
      }
      return true;
    });

    setCart(updatedCart);
    localStorage.setItem("booking-cart", JSON.stringify(updatedCart));
  };

  const handleSubmitAll = async () => {
    const endpoints = [
      {
        type: "booking",
        items: bookingItems,
        url: "/orders/new",
        transformData: (items) => items.map((item) => ({
          id: item.id,
          count: item.quantity,
          online: item.online,
        })),
      },
      {
        type: "purchase",
        items: purchaseItems,
        url: "/purchases/new",
        transformData: (items) => items.map((item) => ({
          id: item.id,
          count: item.quantity,
        })),
      },
    ];

    for (const { type, items, url, transformData } of endpoints) {
      if (items.length > 0) {
        const data = transformData(items);
        try {
          await api.post(url, data);
          localStorage.removeItem("booking-cart");
          navigate("/user/history");
        } catch {}
      }
    }
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
                <th className="col-1">Book Online</th>
              </tr>
            </thead>
            <tbody>
              <CartItems
                items={bookingItems}
                onQuantityChange={handleQuantityChange}
                onOnlineToggle={handleOnlineToggle}
              />
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
              <CartItems
                items={purchaseItems}
                onQuantityChange={handleQuantityChange}
                onOnlineToggle={handleOnlineToggle}
              />
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
};

export default Cart;
