import React, { useState } from "react";
import { findRoutesBetweenLocations } from "../api/adminService";
import { RouteDTO } from "../types";

const FindRoutesBetweenLocations: React.FC = () => {
  const [fromLocation, setFromLocation] = useState("");
  const [toLocation, setToLocation] = useState("");
  const [sortBy, setSortBy] = useState("name");
  const [routes, setRoutes] = useState<RouteDTO[]>([]);
  const [error, setError] = useState<string | null>(null);

  const handleSearch = async () => {
    try {
      const result = await findRoutesBetweenLocations(
        fromLocation,
        toLocation,
        sortBy
      );
      setRoutes(result);
    } catch (err) {
      setError("Failed to fetch routes.");
    }
  };

  return (
    <div className="find-routes">
      <h2>Find Routes Between Locations</h2>
      <input
        type="text"
        placeholder="From Location"
        value={fromLocation}
        onChange={(e) => setFromLocation(e.target.value)}
      />
      <input
        type="text"
        placeholder="To Location"
        value={toLocation}
        onChange={(e) => setToLocation(e.target.value)}
      />
      <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
        <option value="name">Name</option>
        <option value="distance">Distance</option>
        <option value="rating">Rating</option>
      </select>
      <button onClick={handleSearch}>Search</button>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <ul>
        {routes.map((route) => (
          <li key={route.id}>
            {route.name} - {route.distance} km
          </li>
        ))}
      </ul>
    </div>
  );
};

export default FindRoutesBetweenLocations;
