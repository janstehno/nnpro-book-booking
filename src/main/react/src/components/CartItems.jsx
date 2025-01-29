import React from "react";

import CartItemType from "@/utils/CartItemType";

const CartItems = ({ items, onQuantityChange, onOnlineToggle }) => {
  return (
    <>
      {items.map((item) => (
        <tr key={`${item.id}-${item.online}`}>
          <td>{item.title}</td>
          {item.online ? (
            <>
              <td></td>
              <td>
                <input
                  type="checkbox"
                  checked={item.online}
                  onChange={() => onOnlineToggle(item)}
                />
              </td>
            </>
          ) : (
            <>
              <td>
                <button
                  className="btn btn-light minus p-1"
                  onClick={() => onQuantityChange(item, -1)}
                  disabled={item.quantity <= 0}
                >
                  <img className="w-100 p-0 m-0" src="/minus.png" alt="Minus" />
                </button>
                {item.quantity}
                <button
                  className="btn btn-light plus p-1"
                  onClick={() => onQuantityChange(item, 1)}
                  disabled={(item.type === CartItemType.PURCHASE && item.quantity >= 10) ||
                  (item.type === CartItemType.BOOKING && item.quantity >= item.physicalCopies)}
                >
                  <img className="w-100 p-0 m-0" src="/plus.png" alt="Plus" />
                </button>
              </td>
              <td>
                {item.type === CartItemType.PURCHASE ? (
                    new Intl.NumberFormat('en-US', {
                      style: 'currency',
                      currency: 'USD',
                    }).format(item.ebookPrice * item.quantity)
                  ) : null}
              </td>
            </>
          )}
        </tr>
      ))}
    </>
  );
};

export default CartItems;
