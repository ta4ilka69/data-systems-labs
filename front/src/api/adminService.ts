import api from "./axios";
import { RouteDTO, UserDTO } from "../types";

export const getAllAdminRoleRequests = async (): Promise<UserDTO[]> => {
  const response = await api.get<UserDTO[]>("/admin/admin-role-requests");
  return response.data;
};

export const approveAdminRoleRequest = async (
  userId: number
): Promise<void> => {
  await api.post(`/admin/approve-admin-role/${userId}`);
};

export const findRoutesBetweenLocations = async (
  fromLocation: string,
  toLocation: string,
  sortBy: string
): Promise<RouteDTO[]> => {
  const response = await api.get<RouteDTO[]>(
    `/routes/searchBetweenLocations`,
    { params: { fromLocation, toLocation, sortBy } }
  );
  return response.data;
};

export const getRoutesByRatingLessThan = async (
  rating: number
): Promise<RouteDTO[]> => {
  const response = await api.get<RouteDTO[]>(
    `/routes/ratingLessThan/${rating}`
  );
  return response.data;
};

export const deleteRoutesByRating = async (rating: number): Promise<void> => {
  await api.delete(`/routes/rating/${rating}`);
};
