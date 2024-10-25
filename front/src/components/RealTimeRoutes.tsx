import React, { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

interface RealTimeRoutesProps {
  onUpdate: () => void;
}

const RealTimeRoutes: React.FC<RealTimeRoutesProps> = ({ onUpdate }) => {
  const [client, setClient] = useState<Client | null>(null);
  const token = localStorage.getItem("token");
  useEffect(() => {
    const newClient = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 1000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    newClient.onConnect = () => {
      console.log("Connected to STOMP");
      newClient.subscribe("/topic/routes", (message) => {
        console.log("Received:", message.body);
        onUpdate();
      });
    };

    newClient.onStompError = function (frame) {
      console.log("Broker reported error: " + frame.headers["message"]);
      console.log("Additional details: " + frame.body);
    };

    newClient.activate();

    setClient(newClient);

    return () => {
      if (newClient.active) {
        newClient.deactivate();
      }
    };
  }, [onUpdate]);

  return null;
};

export default RealTimeRoutes;
