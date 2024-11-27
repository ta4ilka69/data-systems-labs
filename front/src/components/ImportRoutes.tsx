import React, { useState } from "react";
import api from "../api/axios";
import "./ImportRoutes.css";

const ImportRoutes: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
      setError(null);
      setMessage(null);
    }
  };

  const handleImport = async () => {
    if (!file) {
      setError("Пожалуйста, выберите файл для импорта.");
      return;
    }

    setIsLoading(true);
    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await api.post("/routes/import", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      setMessage(response.data);
      setError(null);
      setFile(null);
      // Reset the file input
      const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
      if (fileInput) fileInput.value = '';
    } catch (err: any) {
      setError(err.response?.data || "Ошибка при импорте маршрутов.");
      setMessage(null);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="import-routes">
      <h2>Массовый импорт маршрутов</h2>
      <div className="upload-section">
        <label className="file-input-label">
          Выберите файл
          <input
            type="file"
            accept=".yaml,.yml"
            onChange={handleFileChange}
          />
        </label>
        <span className="file-name">
          {file ? file.name : 'Файл не выбран'}
        </span>
        <button 
          onClick={handleImport} 
          disabled={!file || isLoading}
        >
          {isLoading ? 'Импортирование...' : 'Импортировать'}
        </button>
      </div>
      
      {message && <div className="message success">{message}</div>}
      {error && <div className="message error">{error}</div>}
    </div>
  );
};

export default ImportRoutes;