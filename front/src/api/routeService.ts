import api from "./axios";
import { RouteDTO } from "../types";

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
