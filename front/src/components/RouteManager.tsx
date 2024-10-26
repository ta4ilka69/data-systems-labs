import React, { useEffect, useState } from "react";
import { getAllRoutes, deleteRoute } from "../api/routeService";
import { RouteDTO } from "../types";
import RealTimeRoutes from "./RealTimeRoutes";
import CreateRoute from "./CreateRoute";
import UpdateRoute from "./UpdateRoute";
import "./RouteManager.css";

const RouteManager: React.FC = () => {
  const [routes, setRoutes] = useState<RouteDTO[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [routesPerPage] = useState(8);
  const [filter, setFilter] = useState("");
  const [sortConfig, setSortConfig] = useState<{
    key: keyof RouteDTO;
    direction: "ascending" | "descending";
  } | null>(null);
  const [selectedRoute, setSelectedRoute] = useState<RouteDTO | null>(null);
  const [showCreate, setShowCreate] = useState(false);

  const currentUserId = Number(localStorage.getItem("userId"));
  const currentUserRole = localStorage.getItem("role");

  useEffect(() => {
    fetchRoutes();
  }, []);

  const fetchRoutes = async () => {
    try {
      const routeList = await getAllRoutes();
      setRoutes(routeList);
    } catch (err) {
      console.error("Failed to fetch routes", err);
      setError("Failed to fetch routes.");
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteRoute(id);
      setRoutes(routes.filter((route) => route.id !== id));
    } catch (err) {
      console.error("Failed to delete route", err);
      setError("You do not have permission to delete this route.");
    }
  };

  const handleSort = (key: keyof RouteDTO) => {
    let direction: "ascending" | "descending" = "ascending";
    if (
      sortConfig &&
      sortConfig.key === key &&
      sortConfig.direction === "ascending"
    ) {
      direction = "descending";
    }
    setSortConfig({ key, direction });
  };

  const filteredRoutes = routes.filter((route) =>
    route.name.toLowerCase().includes(filter.toLowerCase())
  );

  const sortedRoutes = React.useMemo(() => {
    if (sortConfig !== null) {
      return [...filteredRoutes].sort((a, b) => {
        const aValue = a[sortConfig.key];
        const bValue = b[sortConfig.key];

        if (aValue === undefined || bValue === undefined) {
          return 0;
        }

        if (aValue < bValue) {
          return sortConfig.direction === "ascending" ? -1 : 1;
        }
        if (aValue > bValue) {
          return sortConfig.direction === "ascending" ? 1 : -1;
        }
        return 0;
      });
    }
    return filteredRoutes;
  }, [filteredRoutes, sortConfig]);

  const indexOfLastRoute = currentPage * routesPerPage;
  const indexOfFirstRoute = indexOfLastRoute - routesPerPage;
  const currentRoutes = sortedRoutes.slice(indexOfFirstRoute, indexOfLastRoute);

  const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

  return (
    <div className="route-manager">
      <h1>Route Manager</h1>
      <button onClick={() => setShowCreate(true)}>Create New Route</button>
      <input
        type="text"
        placeholder="Filter by Name"
        value={filter}
        onChange={(e) => setFilter(e.target.value)}
      />
      <table>
        <thead>
          <tr>
            <th onClick={() => handleSort("id")}>ID</th>
            <th onClick={() => handleSort("name")}>Name</th>
            <th onClick={() => handleSort("distance")}>Distance (km)</th>
            <th onClick={() => handleSort("rating")}>Rating</th>
            <th>Coordinates</th>
            <th>From</th>
            <th>To</th>
            <th>Editable</th>
            <th>Created By</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {currentRoutes.map((route) => (
            <tr key={route.id}>
              <td>{route.id}</td>
              <td>{route.name}</td>
              <td>{route.distance}</td>
              <td>{route.rating}</td>
              <td>{`(${route.coordinates.x}, ${route.coordinates.y})`}</td>
              <td>{route.from.name}</td>
              <td>{route.to?.name || "N/A"}</td>
              <td>{route.allowAdminEditing ? "Yes" : "No"}</td>
              <td>{route.createdByUsername}</td>
              <td>
                <>
                  {(route.createdById === currentUserId ||
                    currentUserRole === "ADMIN") &&
                    route.allowAdminEditing && (
                      <button onClick={() => setSelectedRoute(route)}>
                        Edit
                      </button>
                    )}
                  <button onClick={() => handleDelete(route.id!)}>
                    Delete
                  </button>{" "}
                </>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <Pagination
        routesPerPage={routesPerPage}
        totalRoutes={sortedRoutes.length}
        paginate={paginate}
        currentPage={currentPage}
        error={error}
      />
      {showCreate && (
        <CreateRoute
          onClose={() => setShowCreate(false)}
          onCreate={fetchRoutes}
        />
      )}
      {selectedRoute && (
        <UpdateRoute
          route={selectedRoute}
          onClose={() => setSelectedRoute(null)}
          onUpdate={fetchRoutes}
        />
      )}
      <RealTimeRoutes onUpdate={fetchRoutes} />
    </div>
  );
};

const Pagination: React.FC<{
  routesPerPage: number;
  totalRoutes: number;
  paginate: (pageNumber: number) => void;
  currentPage: number;
  error: string | null;
}> = ({ routesPerPage, totalRoutes, paginate, currentPage, error }) => {
  const pageNumbers = [];

  for (let i = 1; i <= Math.ceil(totalRoutes / routesPerPage); i++) {
    pageNumbers.push(i);
  }

  return (
    <nav>
      <ul className="pagination">
        {pageNumbers.map((number) => (
          <li key={number} className={number === currentPage ? "active" : ""}>
            <a onClick={() => paginate(number)} href="#!">
              {number}
            </a>
          </li>
        ))}
      </ul>
      {error && <p style={{ color: "red" }}>{error}</p>}
    </nav>
  );
};

export default RouteManager;
