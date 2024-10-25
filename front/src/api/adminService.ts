import { UserDTO } from "../types";

export const getAllAdminRoleRequests = async (): Promise<UserDTO[]> => {
  const response = await fetch("/api/admin/admin-role-requests");
  return response.json();
};

export const approveAdminRoleRequest = async (
  userId: number
): Promise<void> => {
  await fetch(`/api/admin/approve-admin-role/${userId}`, { method: "POST" });
};
