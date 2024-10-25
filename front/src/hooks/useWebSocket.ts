import { useEffect, useState } from "react";

const useWebSocket = (url: string) => {
  const [data, setData] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const socket = new WebSocket(url);

    socket.onopen = () => {
      console.log(`WebSocket connected to ${url}`);
    };

    socket.onmessage = (event) => {
      try {
        const messageData = JSON.parse(event.data);
        setData(messageData);
      } catch (e) {
        console.error("Failed to parse WebSocket message:", e);
      }
    };

    socket.onerror = (err) => {
      console.error("WebSocket error:", err);
      setError("WebSocket connection error");
    };

    socket.onclose = () => {
      console.log("WebSocket connection closed");
    };

    return () => {
      socket.close();
    };
  }, [url]);

  return { data, error };
};

export default useWebSocket;
