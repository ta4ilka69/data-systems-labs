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

  // Filter States
  const [filterId, setFilterId] = useState<string>("");
  const [filterName, setFilterName] = useState<string>("");
  const [filterDistance, setFilterDistance] = useState<string>("");
  const [filterRating, setFilterRating] = useState<string>("");
  const [filterFrom, setFilterFrom] = useState<string>("");
  const [filterTo, setFilterTo] = useState<string>("");
  const [filterCreatedBy, setFilterCreatedBy] = useState<string>("");
  const [filterEditable, setFilterEditable] = useState<string>("");

  const [sortConfig, setSortConfig] = useState<{
    key: keyof RouteDTO | "coordinates" | "from" | "to" | "createdByUsername";
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

  const handleSort = (
    key: keyof RouteDTO | "coordinates" | "from" | "to" | "createdByUsername"
  ) => {
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

  const filteredRoutes = routes.filter((route) => {
    const matchesId = filterId
      ? route.id?.toString().includes(filterId) || false
      : true;
    const matchesName = filterName
      ? route.name.toLowerCase().includes(filterName.toLowerCase())
      : true;
    const matchesDistance = filterDistance
      ? route.distance?.toString().includes(filterDistance)
      : true;
    const matchesRating = filterRating
      ? route.rating.toString().includes(filterRating)
      : true;
    const matchesFrom = filterFrom
      ? route.from.name.toLowerCase().includes(filterFrom.toLowerCase())
      : true;
    const matchesTo = filterTo
      ? route.to
        ? route.to.name.toLowerCase().includes(filterTo.toLowerCase())
        : false
      : true;
    const matchesCreatedBy = filterCreatedBy
      ? route.createdByUsername
        ? route.createdByUsername
          .toLowerCase()
          .includes(filterCreatedBy.toLowerCase())
        : false
      : true;
    const matchesEditable = filterEditable
      ? filterEditable === "Yes"
        ? route.allowAdminEditing
        : !route.allowAdminEditing
      : true;

    return (
      matchesId &&
      matchesName &&
      matchesDistance &&
      matchesRating &&
      matchesFrom &&
      matchesTo &&
      matchesCreatedBy &&
      matchesEditable
    );
  });

  const sortedRoutes = React.useMemo(() => {
    if (sortConfig !== null) {
      return [...filteredRoutes].sort((a, b) => {
        let aValue: any;
        let bValue: any;

        if (sortConfig.key === "coordinates") {
          aValue = `${a.coordinates.x},${a.coordinates.y}`;
          bValue = `${b.coordinates.x},${b.coordinates.y}`;
        } else if (sortConfig.key === "from" || sortConfig.key === "to") {
          aValue = `${a[sortConfig.key]?.name || ""} (${a[
            sortConfig.key
          ]?.x}, ${a[sortConfig.key]?.y})`;
          bValue = `${b[sortConfig.key]?.name || ""} (${b[
            sortConfig.key
          ]?.x}, ${b[sortConfig.key]?.y})`;
        } else {
          aValue = a[sortConfig.key];
          bValue = b[sortConfig.key];
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
  const currentRoutes = sortedRoutes.slice(
    indexOfFirstRoute,
    indexOfLastRoute
  );

  const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

  return (
    <div className="route-manager">
      <h1>Route Manager</h1>
      <button onClick={() => setShowCreate(true)}>Create New Route</button>

      {/* Filter Section */}
      <div className="filters">
        <h3>Filters</h3>
        <input
          type="text"
          placeholder="Filter by ID"
          value={filterId}
          onChange={(e) => setFilterId(e.target.value)}
        />
        <input
          type="text"
          placeholder="Filter by Name"
          value={filterName}
          onChange={(e) => setFilterName(e.target.value)}
        />
        <input
          type="text"
          placeholder="Filter by Distance"
          value={filterDistance}
          onChange={(e) => setFilterDistance(e.target.value)}
        />
        <input
          type="text"
          placeholder="Filter by Rating"
          value={filterRating}
          onChange={(e) => setFilterRating(e.target.value)}
        />
        <input
          type="text"
          placeholder="Filter by From Location"
          value={filterFrom}
          onChange={(e) => setFilterFrom(e.target.value)}
        />
        <input
          type="text"
          placeholder="Filter by To Location"
          value={filterTo}
          onChange={(e) => setFilterTo(e.target.value)}
        />
        <input
          type="text"
          placeholder="Filter by Created By"
          value={filterCreatedBy}
          onChange={(e) => setFilterCreatedBy(e.target.value)}
        />
        <select
          value={filterEditable}
          onChange={(e) => setFilterEditable(e.target.value)}
        >
          <option value="">All</option>
          <option value="Yes">Editable</option>
          <option value="No">Not Editable</option>
        </select>
        <button
          onClick={() => {
            setFilterId("");
            setFilterName("");
            setFilterDistance("");
            setFilterRating("");
            setFilterFrom("");
            setFilterTo("");
            setFilterCreatedBy("");
            setFilterEditable("");
          }}
        >
          Reset Filters
        </button>
      </div>

      <table>
        <thead>
          <tr>
            <th onClick={() => handleSort("id")}>ID</th>
            <th onClick={() => handleSort("name")}>Name</th>
            <th onClick={() => handleSort("distance")}>Distance (km)</th>
            <th onClick={() => handleSort("rating")}>Rating</th>
            <th onClick={() => handleSort("coordinates")}>Coordinates</th>
            <th onClick={() => handleSort("from")}>From</th>
            <th onClick={() => handleSort("to")}>To</th>
            <th onClick={() => handleSort("allowAdminEditing")}>Editable</th>
            <th onClick={() => handleSort("createdByUsername")}>
              Created By
            </th>
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
              <td>{`${route.from.name} (${route.from.x}, ${route.from.y})`}</td>
              <td>
                {route.to
                  ? `${route.to.name} (${route.to.x}, ${route.to.y})`
                  : "N/A"}
              </td>
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