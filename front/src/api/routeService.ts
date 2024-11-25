import api from "./axios";
import { CoordinatesDTO, LocationDTO, RouteDTO } from "../types";
import { ImportHistory } from "../types/ImportHistory";

export const getAllRoutes = async (): Promise<RouteDTO[]> => {
  const response = await api.get<RouteDTO[]>("/routes");
  return response.data;
};

export const createRoute = async (route: RouteDTO): Promise<RouteDTO> => {
  const response = await api.post<RouteDTO>("/routes", route);
  return response.data;
};

export const updateRoute = async (route: RouteDTO): Promise<RouteDTO> => {
  if (!route.id) {
    throw new Error("Route ID is required for update.");
  }
  const response = await api.put<RouteDTO>(`/routes/${route.id}`, route);
  return response.data;
};

export const deleteRoute = async (id: number): Promise<void> => {
  await api.delete(`/routes/${id}`);
};

export const getAllCoordinates = async (): Promise<CoordinatesDTO[]> => {
  const response = await api.get<CoordinatesDTO[]>("/coordinates");
  return response.data;
};

export const getAllLocations = async (): Promise<LocationDTO[]> => {
  const response = await api.get<LocationDTO[]>("/locations");
  return response.data;
};

export const importRoutes = async (file: File): Promise<string> => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await api.post("/api/routes/import", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  return response.data;
};
export const getImportHistory = async (): Promise<ImportHistory[]> => {
  const response = await api.get<ImportHistory[]>("/import");
  return response.data;
};