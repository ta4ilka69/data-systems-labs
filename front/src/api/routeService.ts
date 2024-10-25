import { RouteDTO } from "../types";

export const getAllRoutes = async (): Promise<RouteDTO[]> => {
  const response = await fetch("/api/routes");
  return response.json();
};

export const createRoute = async (route: RouteDTO): Promise<RouteDTO> => {
  const response = await fetch("/api/routes", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(route),
  });
  return response.json();
};

export const updateRoute = async (route: RouteDTO): Promise<RouteDTO> => {
  if (!route.id) {
    throw new Error("Route ID is required for update.");
  }
  const response = await fetch(`/api/routes/${route.id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(route),
  });
  return response.json();
};

export const deleteRoute = async (id: number): Promise<void> => {
  await fetch(`/api/routes/${id}`, { method: "DELETE" });
};