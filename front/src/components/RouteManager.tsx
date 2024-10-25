import React, { useEffect, useState } from "react";
import { getAllRoutes } from "../api/routeService";
import { RouteDTO } from "../types";

const RouteManager: React.FC = () => {
  const [routes, setRoutes] = useState<RouteDTO[]>([]);

  useEffect(() => {
    const fetchRoutes = async () => {
      try {
        const routeList = await getAllRoutes();
        setRoutes(routeList);
      } catch (err) {
        console.error("Failed to fetch routes", err);
      }
    };
    fetchRoutes();
  }, []);

  return (
    <div>
      <h2>Route Manager</h2>
      <ul>
        {routes.map((route) => (
          <li key={route.id}>
            {route.name} - Distance: {route.distance} km - Created by:{" "}
            {route.createdByUsername}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default RouteManager;
