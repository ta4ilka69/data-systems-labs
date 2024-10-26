import React, { useState, useEffect } from "react";
import {
  updateRoute,
  getAllLocations,
  getAllCoordinates,
} from "../api/routeService";
import { RouteDTO, CoordinatesDTO, LocationDTO } from "../types";

interface UpdateRouteProps {
  route: RouteDTO;
  onClose: () => void;
  onUpdate: () => void;
}

const UpdateRoute: React.FC<UpdateRouteProps> = ({
  route,
  onClose,
  onUpdate,
}) => {
  const [name, setName] = useState(route.name);
  const [distance, setDistance] = useState<number | undefined>(route.distance);
  const [rating, setRating] = useState<number>(route.rating);
  const [xCoord, setXCoord] = useState<number>(route.coordinates.x);
  const [yCoord, setYCoord] = useState<number>(route.coordinates.y);
  const [fromX, setFromX] = useState<number>(route.from.x);
  const [fromY, setFromY] = useState<number>(route.from.y);
  const [fromName, setFromName] = useState(route.from.name);
  const [toX, setToX] = useState<number>(route.to?.x || 0);
  const [toY, setToY] = useState<number>(route.to?.y || 0);
  const [toName, setToName] = useState(route.to?.name || "");
  const [allowAdminEditing, setAllowAdminEditing] = useState(
    route.allowAdminEditing
  );
  const [error, setError] = useState<string | null>(null);
  const [existingLocations, setExistingLocations] = useState<LocationDTO[]>([]);
  const [existingCoordinates, setExistingCoordinates] = useState<
    CoordinatesDTO[]
  >([]);

  useEffect(() => {
    const fetchLocations = async () => {
      try {
        const locations = await getAllLocations();
        setExistingLocations(locations);
      } catch (err) {
        console.error("Failed to fetch locations", err);
      }
    };

    const fetchCoordinates = async () => {
      try {
        const coordinates = await getAllCoordinates();
        setExistingCoordinates(coordinates);
      } catch (err) {
        console.error("Failed to fetch coordinates", err);
      }
    };

    fetchLocations();
    fetchCoordinates();
  }, []);

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
    if (yCoord >= 552) {
      setError("Y Coordinate must be less than 552.");
      return;
    }

    const coordinates: CoordinatesDTO = { x: xCoord, y: yCoord };
    const from: LocationDTO = { x: fromX, y: fromY, name: fromName };
    const to: LocationDTO | undefined = toName.trim()
      ? { x: toX, y: toY, name: toName }
      : undefined;

    const updatedRoute: RouteDTO = {
      ...route,
      name,
      distance,
      rating,
      coordinates,
      from,
      to,
      allowAdminEditing,
    };

    try {
      await updateRoute(updatedRoute);
      onUpdate();
      onClose();
    } catch (err) {
      setError("Failed to update route.");
    }
  };

  const handleNumberInput = (value: string) => {
    return parseFloat(value) || 0;
  };

  return (
    <div className="modal">
      <h3>Update Route</h3>
      <input
        type="text"
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="Route Name"
      />
      <input
        type="number"
        value={distance}
        onChange={(e) => setDistance(handleNumberInput(e.target.value))}
        placeholder="Distance (km)"
      />
      <input
        type="number"
        value={rating}
        onChange={(e) => setRating(handleNumberInput(e.target.value))}
        placeholder="Rating"
      />
      <h4>Coordinates</h4>
      <select
        onChange={(e) => {
          const selectedCoordinate = existingCoordinates.find(
            (coord) => `${coord.x},${coord.y}` === e.target.value
          );
          if (selectedCoordinate) {
            setXCoord(selectedCoordinate.x);
            setYCoord(selectedCoordinate.y);
          }
        }}
      >
        <option value="">Select Existing Coordinates</option>
        {existingCoordinates.map((coord) => (
          <option key={`${coord.x},${coord.y}`} value={`${coord.x},${coord.y}`}>
            ({coord.x}, {coord.y})
          </option>
        ))}
      </select>
      <input
        type="number"
        value={xCoord}
        onChange={(e) => setXCoord(handleNumberInput(e.target.value))}
        placeholder="X Coordinate"
      />
      <input
        type="number"
        value={yCoord}
        onChange={(e) => setYCoord(handleNumberInput(e.target.value))}
        placeholder="Y Coordinate (max 552)"
      />
      <h4>From Location</h4>
      <select
        onChange={(e) => {
          const selectedLocation = existingLocations.find(
            (loc) => loc.name === e.target.value
          );
          if (selectedLocation) {
            setFromX(selectedLocation.x);
            setFromY(selectedLocation.y);
            setFromName(selectedLocation.name);
          }
        }}
      >
        <option value="">Select Existing Location</option>
        {existingLocations.map((loc) => (
          <option key={loc.name} value={loc.name}>
            {loc.name} ({loc.x}, {loc.y})
          </option>
        ))}
      </select>
      <input
        type="number"
        value={fromX}
        onChange={(e) => setFromX(handleNumberInput(e.target.value))}
        placeholder="From X"
      />
      <input
        type="number"
        value={fromY}
        onChange={(e) => setFromY(handleNumberInput(e.target.value))}
        placeholder="From Y"
      />
      <input
        type="text"
        value={fromName}
        onChange={(e) => setFromName(e.target.value)}
        placeholder="From Location Name"
      />
      <h4>To Location (Optional)</h4>
      <select
        onChange={(e) => {
          const selectedLocation = existingLocations.find(
            (loc) => loc.name === e.target.value
          );
          if (selectedLocation) {
            setToX(selectedLocation.x);
            setToY(selectedLocation.y);
            setToName(selectedLocation.name);
          }
        }}
      >
        <option value="">Select Existing Location</option>
        {existingLocations.map((loc) => (
          <option key={loc.name} value={loc.name}>
            {loc.name} ({loc.x}, {loc.y})
          </option>
        ))}
      </select>
      <input
        type="number"
        value={toX}
        onChange={(e) => setToX(handleNumberInput(e.target.value))}
        placeholder="To X"
      />
      <input
        type="number"
        value={toY}
        onChange={(e) => setToY(handleNumberInput(e.target.value))}
        placeholder="To Y"
      />
      <input
        type="text"
        value={toName}
        onChange={(e) => setToName(e.target.value)}
        placeholder="To Location Name"
      />
      <div>
        <label>
          Allow Admin Editing:
          <input
            type="checkbox"
            checked={allowAdminEditing}
            onChange={(e) => setAllowAdminEditing(e.target.checked)}
          />
        </label>
      </div>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <button onClick={handleSubmit}>Update</button>
      <button onClick={onClose}>Cancel</button>
    </div>
  );
};

export default UpdateRoute;
