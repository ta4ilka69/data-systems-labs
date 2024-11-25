import React, { useState } from "react";
import axios from "axios";

const ImportRoutes: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  const handleImport = async () => {
    if (!file) {
      setError("Пожалуйста, выберите файл для импорта.");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await axios.post("/api/routes/import", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      setMessage(response.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data || "Ошибка при импорте маршрутов.");
      setMessage(null);
    }
  };

  return (
    <div className="import-routes">
      <h2>Массовый импорт маршрутов</h2>
      <input type="file" accept=".yaml,.yml" onChange={handleFileChange} />
      <button onClick={handleImport}>Импортировать</button>
      {message && <p style={{ color: "green" }}>{message}</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
};

export default ImportRoutes;