import React, { useEffect } from "react";
import useWebSocket from "../hooks/useWebSocket";

interface RealTimeRoutesProps {
  onUpdate: () => void;
}

const RealTimeRoutes: React.FC<RealTimeRoutesProps> = ({ onUpdate }) => {
  const { data, error } = useWebSocket("ws://localhost:8080/api/routes");

  useEffect(() => {
    if (data) {
      console.log("New route data received:", data);
      onUpdate();
    }
  }, [data, onUpdate]);

  useEffect(() => {
    if (error) {
      console.error(error);
    }
  }, [error]);

  return null;
};

export default RealTimeRoutes;
