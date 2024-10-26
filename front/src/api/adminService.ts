import api from "./axios";
import { UserDTO } from "../types";

export const getAllAdminRoleRequests = async (): Promise<UserDTO[]> => {
  const response = await api.get<UserDTO[]>("/admin/admin-role-requests");
  return response.data;
};

export const approveAdminRoleRequest = async (
  userId: number
): Promise<void> => {
  await api.post(`/admin/approve-admin-role/${userId}`);
};
