import React, { useEffect, useState } from "react";
import { getImportHistory } from "../api/routeService";
import RealTimeImportHistory from "./RealTimeImportHistory";
import { ImportHistory } from "../types/ImportHistory";
import "./ImportHistory.css";

const ImportHistoryComponent: React.FC = () => {
  const [importHistory, setImportHistory] = useState<ImportHistory[]>([]);
  const [error, setError] = useState<string | null>(null);

  const fetchImportHistory = async () => {
    try {
      const history = await getImportHistory();
      setImportHistory(history);
    } catch (err) {
      console.error("Failed to fetch import history", err);
      setError("Failed to fetch import history.");
    }
  };

  useEffect(() => {
    fetchImportHistory();
  }, []);

  return (
    <div className="import-history">
      <h2>Import History</h2>
      {error && <p className="error-message">{error}</p>}
      <RealTimeImportHistory onUpdate={fetchImportHistory} />
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Timestamp</th>
            <th>Status</th>
            <th>Performed By</th>
            <th>Records Imported</th>
            <th>Error Message</th>
          </tr>
        </thead>
        <tbody>
          {importHistory.map((history) => (
            <tr key={history.id}>
              <td>{history.id}</td>
              <td>{new Date(history.timestamp).toLocaleString()}</td>
              <td className={`status-${history.status.toLowerCase()}`}>
                {history.status}
              </td>
              <td>{history.performedBy}</td>
              <td>{history.recordsImported}</td>
              <td className="error-message">{history.errorMessage || "N/A"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ImportHistoryComponent;