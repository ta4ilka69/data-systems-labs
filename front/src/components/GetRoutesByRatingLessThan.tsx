import React, { useState } from "react";
import { getRoutesByRatingLessThan } from "../api/adminService";
import { RouteDTO } from "../types";

const GetRoutesByRatingLessThan: React.FC = () => {
  const [rating, setRating] = useState<number>(0);
  const [routes, setRoutes] = useState<RouteDTO[]>([]);
  const [error, setError] = useState<string | null>(null);

  const handleSearch = async () => {
    try {
      const result = await getRoutesByRatingLessThan(rating);
      setRoutes(result);
    } catch (err) {
      setError("Failed to fetch routes.");
    }
  };

  return (
    <div className="get-routes">
      <h2>Get Routes By Rating Less Than</h2>
      <input
        type="number"
        placeholder="Rating"
        value={rating}
        onChange={(e) => setRating(Number(e.target.value))}
      />
      <button onClick={handleSearch}>Search</button>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <ul>
        {routes.map((route) => (
          <li key={route.id}>
            {route.name} - {route.rating}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default GetRoutesByRatingLessThan;
