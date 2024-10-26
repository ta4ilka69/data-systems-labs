import React, { useEffect, useState } from "react";
import {
  MapContainer,
  TileLayer,
  Polyline,
  Marker,
  Popup,
  useMap,
} from "react-leaflet";
import L from "leaflet";
import { getAllRoutes, updateRoute } from "../api/routeService";
import { RouteDTO } from "../types";
import "./MapView.css";

// Function to generate a color based on user ID
const getColorByUser = (userId: number) => {
  const colors = [
    "#e6194B",
    "#3cb44b",
    "#ffe119",
    "#4363d8",
    "#f58231",
    "#911eb4",
    "#46f0f0",
    "#f032e6",
    "#bcf60c",
    "#fabebe",
    "#008080",
    "#e6beff",
    "#9A6324",
    "#fffac8",
    "#800000",
    "#aaffc3",
    "#808000",
    "#ffd8b1",
    "#000075",
    "#808080",
    "#ffffff",
    "#000000",
  ];
  return colors[userId % colors.length];
};

// Function to convert x, y to latitude and longitude
const convertToLatLng = (x: number, y: number): [number, number] => {
  const lat = (y / 552) * 180 - 90; // Map y to -90 to 90
  const lng = (x / 552) * 360 - 180; // Map x to -180 to 180
  return [lat, lng];
};

// Function to convert latitude and longitude back to x, y
const convertToXY = (lat: number, lng: number): [number, number] => {
  const y = ((lat + 90) / 180) * 552;
  const x = ((lng + 180) / 360) * 552;
  return [x, y];
};

// Function to center the map on the user's location
const SetViewOnUserLocation: React.FC<{ position: [number, number] }> = ({
  position,
}) => {
  const map = useMap();
  map.setView(position, 13);
  return null;
};

const MapView: React.FC = () => {
  const [routes, setRoutes] = useState<RouteDTO[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [userPosition, setUserPosition] = useState<[number, number] | null>(
    null
  );

  useEffect(() => {
    const fetchRoutesData = async () => {
      try {
        const routeList = await getAllRoutes();
        setRoutes(routeList);
      } catch (err) {
        console.error("Failed to fetch routes", err);
        setError("Failed to fetch routes.");
      }
    };

    fetchRoutesData();
  }, []);

  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setUserPosition([
            position.coords.latitude,
            position.coords.longitude,
          ]);
        },
        (err) => {
          console.error("Failed to get user location", err);
        }
      );
    }
  }, []);

  const handleMarkerDragEnd = async (
    route: RouteDTO,
    position: [number, number],
    isFrom: boolean
  ) => {
    const [x, y] = convertToXY(position[0], position[1]);
    const updatedRoute = { ...route };

    if (isFrom) {
      updatedRoute.from.x = x;
      updatedRoute.from.y = y;
    } else if (updatedRoute.to) {
      updatedRoute.to.x = x;
      updatedRoute.to.y = y;
    }

    try {
      await updateRoute(updatedRoute);
      setRoutes((prevRoutes) =>
        prevRoutes.map((r) => (r.id === route.id ? updatedRoute : r))
      );
    } catch (err) {
      console.error("Failed to update route", err);
      setError("Failed to update route.");
    }
  };

  return (
    <MapContainer
      center={[51.505, -0.09]} // Default center; adjust as needed
      zoom={13}
      style={{ height: "100vh", width: "100vw" }}
    >
      {/* Center map on user location if available */}
      {userPosition && <SetViewOnUserLocation position={userPosition} />}

      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      {routes.map((route) => {
        const from = convertToLatLng(route.from.x, route.from.y);
        const to = route.to ? convertToLatLng(route.to.x, route.to.y) : null;
        const color = getColorByUser(route.createdById || 0);
        const positions = to ? [from, to] : [from];

        return (
          <React.Fragment key={route.id}>
            {to && (
              <Polyline positions={positions} color={color}>
                <Popup>
                  <div>
                    <h3>{route.name}</h3>
                    <p>Distance: {route.distance} km</p>
                    <p>Rating: {route.rating}</p>
                    <p>Created by: {route.createdByUsername}</p>
                  </div>
                </Popup>
              </Polyline>
            )}
            <Marker
              position={from}
              draggable={true}
              eventHandlers={{
                dragend: (e) => {
                  const marker = e.target;
                  const position = marker.getLatLng();
                  handleMarkerDragEnd(
                    route,
                    [position.lat, position.lng],
                    true
                  );
                },
              }}
            >
              <Popup>
                <div>
                  <h3>{route.name}</h3>
                  <p>Distance: {route.distance} km</p>
                  <p>Rating: {route.rating}</p>
                  <p>Created by: {route.createdByUsername}</p>
                </div>
              </Popup>
            </Marker>
            {to && (
              <Marker
                position={to}
                draggable={true}
                eventHandlers={{
                  dragend: (e) => {
                    const marker = e.target;
                    const position = marker.getLatLng();
                    handleMarkerDragEnd(
                      route,
                      [position.lat, position.lng],
                      false
                    );
                  },
                }}
              >
                <Popup>
                  <div>
                    <h3>{route.name}</h3>
                    <p>Distance: {route.distance} km</p>
                    <p>Rating: {route.rating}</p>
                    <p>Created by: {route.createdByUsername}</p>
                  </div>
                </Popup>
              </Marker>
            )}
          </React.Fragment>
        );
      })}
      {/* Current user location marker */}
      {userPosition && (
        <Marker
          position={userPosition}
          icon={L.icon({
            iconUrl: "https://www.svgrepo.com/show/504557/maps.svg",
            iconSize: [25, 41],
            iconAnchor: [12, 41],
            popupAnchor: [1, -34],
            shadowUrl:
              "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png",
            shadowSize: [41, 41],
          })}
        >
          <Popup>You are here</Popup>
        </Marker>
      )}
    </MapContainer>
  );
};

export default MapView;
