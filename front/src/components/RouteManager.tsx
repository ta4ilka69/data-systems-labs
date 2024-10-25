import React, { useEffect, useState } from "react";
import { getAllRoutes, deleteRoute } from "../api/routeService";
import { RouteDTO } from "../types";
import RealTimeRoutes from "./RealTimeRoutes";
import CreateRoute from "./CreateRoute";
import UpdateRoute from "./UpdateRoute";

const RouteManager: React.FC = () => {
  const [routes, setRoutes] = useState<RouteDTO[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [routesPerPage] = useState(10);
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
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteRoute(id);
      setRoutes(routes.filter((route) => route.id !== id));
    } catch (err) {
      console.error("Failed to delete route", err);
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

  const sortedRoutes = React.useMemo(() => {
    let sortableRoutes = [...routes];
    if (sortConfig !== null) {
      sortableRoutes.sort((a, b) => {
        if (a[sortConfig.key]! < b[sortConfig.key]!) {
          return sortConfig.direction === "ascending" ? -1 : 1;
        }
        if (a[sortConfig.key]! > b[sortConfig.key]!) {
          return sortConfig.direction === "ascending" ? 1 : -1;
        }
        return 0;
      });
    }
    return sortableRoutes;
  }, [routes, sortConfig]);

  const filteredRoutes = sortedRoutes.filter((route) =>
    route.name.toLowerCase().includes(filter.toLowerCase())
  );

  // Pagination Logic
  const indexOfLastRoute = currentPage * routesPerPage;
  const indexOfFirstRoute = indexOfLastRoute - routesPerPage;
  const currentRoutes = filteredRoutes.slice(
    indexOfFirstRoute,
    indexOfLastRoute
  );
  const totalPages = Math.ceil(filteredRoutes.length / routesPerPage);

  return (
    <div>
      <h2>Route Manager</h2>
      <div>
        <button onClick={() => setShowCreate(true)}>Create New Route</button>
        {showCreate && (
          <CreateRoute
            onClose={() => setShowCreate(false)}
            onCreate={fetchRoutes}
          />
        )}
      </div>
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
              <td>{route.createdByUsername}</td>
              <td>
                {(route.createdById === currentUserId ||
                  currentUserRole === "ADMIN") && (
                  <>
                    <button onClick={() => setSelectedRoute(route)}>
                      Edit
                    </button>
                    <button onClick={() => handleDelete(route.id!)}>
                      Delete
                    </button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {/* Pagination Controls */}
      <div>
        {Array.from({ length: totalPages }, (_, i) => i + 1).map(
          (pageNumber) => (
            <button
              key={pageNumber}
              onClick={() => setCurrentPage(pageNumber)}
              disabled={currentPage === pageNumber}
            >
              {pageNumber}
            </button>
          )
        )}
      </div>
      {/* Update Route Modal */}
      {selectedRoute && (
        <UpdateRoute
          route={selectedRoute}
          onClose={() => setSelectedRoute(null)}
          onUpdate={fetchRoutes}
        />
      )}
      {/* Real-Time Updates */}
      <RealTimeRoutes onUpdate={fetchRoutes} />
    </div>
  );
};

export default RouteManager;