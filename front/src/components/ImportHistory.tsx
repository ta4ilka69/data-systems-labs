import React, { useEffect, useState } from "react";
import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";

interface ImportHistory {
  id: number;
  timestamp: string;
  status: "SUCCESS" | "PENDING" | "FAILURE";
  performedBy: string;
  recordsImported: number;
  errorMessage: string | null;
}

const ImportHistory: React.FC = () => {
  const [importHistories, setImportHistories] = useState<ImportHistory[]>([]);
  const [error, setError] = useState<string | null>(null);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: (str) => {
        console.log(str);
      },
      reconnectDelay: 5000,
    });

    client.onConnect = () => {
      client.subscribe("/topic/import-history", (message: IMessage) => {
        const updatedImport: ImportHistory = JSON.parse(message.body);
        setImportHistories((prev) => [updatedImport, ...prev]);
      });
    };

    client.activate();

    return () => {
      client.deactivate();
    };
  }, [token]);

  return (
    <div className="import-history">
      <h2>История импорта</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Время</th>
            <th>Статус</th>
            <th>Выполнил</th>
            <th>Добавлено объектов</th>
            <th>Ошибка</th>
          </tr>
        </thead>
        <tbody>
          {importHistories.map((history) => (
            <tr key={history.id}>
              <td>{history.id}</td>
              <td>{new Date(history.timestamp).toLocaleString()}</td>
              <td>{history.status}</td>
              <td>{history.performedBy}</td>
              <td>{history.recordsImported}</td>
              <td>{history.errorMessage || "N/A"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ImportHistory;