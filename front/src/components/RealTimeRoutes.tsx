import React, { useEffect } from "react";
import useWebSocket from "../hooks/useWebSocket";

const RealTimeRoutes: React.FC = () => {
  const newRouteData = useWebSocket("ws://localhost:8080/api/routes/websocket");

  useEffect(() => {
    if (newRouteData) {
      console.log("New route data received:", newRouteData);
    }
  }, [newRouteData]);

  return (
    <div>
      <h3>Real-Time Updates</h3>
      {newRouteData && (
        <div>
          New Route Added: {newRouteData.name} - {newRouteData.distance} km
        </div>
      )}
    </div>
  );
};

export default RealTimeRoutes;
