import React, { useState } from "react";
import { deleteRoutesByRating } from "../api/adminService";

const DeleteRoutesByRating: React.FC = () => {
  const [rating, setRating] = useState<number>(0);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleDelete = async () => {
    try {
      await deleteRoutesByRating(rating);
      setMessage(`Routes with rating ${rating} deleted successfully.`);
    } catch (err) {
      setError("Failed to delete routes.");
    }
  };

  return (
    <div className="delete-routes">
      <h2>Delete Routes By Rating</h2>
      <input
        type="number"
        placeholder="Rating"
        value={rating}
        onChange={(e) => setRating(Number(e.target.value))}
      />
      <button onClick={handleDelete}>Delete</button>
      {message && <p style={{ color: "green" }}>{message}</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
};

export default DeleteRoutesByRating;
