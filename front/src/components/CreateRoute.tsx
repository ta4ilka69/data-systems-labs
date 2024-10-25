import React, { useState } from "react";
import { createRoute } from "../api/routeService";
import { RouteDTO, CoordinatesDTO, LocationDTO } from "../types";

interface CreateRouteProps {
  onClose: () => void;
  onCreate: () => void;
}

const CreateRoute: React.FC<CreateRouteProps> = ({ onClose, onCreate }) => {
  const [name, setName] = useState("");
  const [distance, setDistance] = useState<number | undefined>(undefined);
  const [rating, setRating] = useState<number>(1);
  const [xCoord, setXCoord] = useState<number>(0);
  const [yCoord, setYCoord] = useState<number>(0);
  const [fromX, setFromX] = useState<number>(0);
  const [fromY, setFromY] = useState<number>(0);
  const [fromName, setFromName] = useState("");
  const [toX, setToX] = useState<number>(0);
  const [toY, setToY] = useState<number>(0);
  const [toName, setToName] = useState("");
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async () => {
    if (!name.trim()) {
      setError("Name cannot be empty.");
      return;
    }
    if (distance !== undefined && distance <= 1) {
      setError("Distance must be greater than 1.");
      return;
    }
    if (rating <= 0) {
      setError("Rating must be greater than 0.");
      return;
    }
    if (!fromName.trim()) {
      setError("From location name cannot be empty.");
      return;
    }

    const coordinates: CoordinatesDTO = { x: xCoord, y: yCoord };
    const from: LocationDTO = { x: fromX, y: fromY, name: fromName };
    const to: LocationDTO | undefined = toName.trim()
      ? { x: toX, y: toY, name: toName }
      : undefined;

    const currentUserRole = localStorage.getItem("role");
    const allowAdminEditing = currentUserRole === "ADMIN";

    const newRoute: RouteDTO = {
      name,
      distance,
      rating,
      coordinates,
      from,
      to,
      allowAdminEditing,
    };

    try {
      await createRoute(newRoute);
      onCreate();
      onClose();
    } catch (err) {
      setError("Failed to create route.");
    }
  };

  return (
    <div className="modal">
      <h3>Create New Route</h3>
      <input
        type="text"
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="Route Name"
      />
      <input
        type="number"
        value={distance}
        onChange={(e) => setDistance(Number(e.target.value))}
        placeholder="Distance (km)"
      />
      <input
        type="number"
        value={rating}
        onChange={(e) => setRating(Number(e.target.value))}
        placeholder="Rating"
      />
      <h4>Coordinates</h4>
      <input
        type="number"
        value={xCoord}
        onChange={(e) => setXCoord(Number(e.target.value))}
        placeholder="X Coordinate"
      />
      <input
        type="number"
        value={yCoord}
        onChange={(e) => setYCoord(Number(e.target.value))}
        placeholder="Y Coordinate"
      />
      <h4>From Location</h4>
      <input
        type="number"
        value={fromX}
        onChange={(e) => setFromX(Number(e.target.value))}
        placeholder="From X"
      />
      <input
        type="number"
        value={fromY}
        onChange={(e) => setFromY(Number(e.target.value))}
        placeholder="From Y"
      />
      <input
        type="text"
        value={fromName}
        onChange={(e) => setFromName(e.target.value)}
        placeholder="From Location Name"
      />
      <h4>To Location (Optional)</h4>
      <input
        type="number"
        value={toX}
        onChange={(e) => setToX(Number(e.target.value))}
        placeholder="To X"
      />
      <input
        type="number"
        value={toY}
        onChange={(e) => setToY(Number(e.target.value))}
        placeholder="To Y"
      />
      <input
        type="text"
        value={toName}
        onChange={(e) => setToName(e.target.value)}
        placeholder="To Location Name"
      />
      {error && <p style={{ color: "red" }}>{error}</p>}
      <button onClick={handleSubmit}>Create</button>
      <button onClick={onClose}>Cancel</button>
    </div>
  );
};

export default CreateRoute;
