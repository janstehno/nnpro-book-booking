import React, { useState, useEffect } from "react";
import api from "~/axios.config";
import { Link, useNavigate, useParams } from "react-router-dom";
import Loading from "@/components/Loading";

const Purchase = () => {
  const { purchaseId } = useParams();
  const [purchase, setPurchase] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchPurchase();
  }, []);

  const fetchPurchase = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/purchases/${purchaseId}`);
      setPurchase(response.data);
    } catch {}
    setLoading(false);
  };

  if (loading) {
    return <Loading />;
  }

  return (
    purchase && (
      <div className="order-container main-container">
        <Link to="/user/history">Back</Link>
        <h1 className="text-primary">Order Details</h1>
        <table className="table table-hover">
          <thead>
            <tr>
              <th scope="col">Title</th>
              <th scope="col">Count</th>
              <th scope="col">Price</th>
            </tr>
          </thead>
          <tbody>
            {purchase.books.map((book) => {
              return (
                <tr key={book.id}>
                  <td>{book.title}</td>
                  <td>{book.count}</td>
                  <td>${book.count * book.price}</td>
                </tr>
              );
            })}
          </tbody>
          <tfoot className="table-group-divider">
            <tr>
              <td className="border-0">Summary</td>
              <td className="border-0">{purchase.books.reduce((total, book) => total + book.count, 0)}</td>
              <td className="border-0">${purchase.books.reduce((total, book) => total + book.price * book.count, 0)}</td>
            </tr>
          </tfoot>
        </table>
      </div>
    )
  );
};

export default Purchase;
