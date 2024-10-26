import api from "./axios";
import {
  AuthRequestDTO,
  AuthResponseDTO,
  RegisterRequestDTO,
  RegisterResponseDTO,
  UserDTO,
} from "../types";

export const loginUser = async (
  authRequest: AuthRequestDTO
): Promise<AuthResponseDTO> => {
  const response = await api.post<AuthResponseDTO>("/auth/login", authRequest);
  return response.data;
};

export const registerUser = async (registerRequest: RegisterRequestDTO) => {
  const response = await api.post<RegisterResponseDTO>(
    "/auth/register",
    registerRequest
  );
  return response.data;
};

export const requestAdminRole = async () => {
  const response = await api.post<string>("/auth/request-admin-role");
  await getUser();
  return response.data;
};

export const getUser = async () => {
  const response = await api.get<UserDTO>("/auth/me");
  const user = response.data;
  localStorage.setItem("username", user.username);
  localStorage.setItem("role", user.roles.includes("ADMIN") ? "ADMIN" : "USER");
  localStorage.setItem("userId", user.id.toString());
  localStorage.setItem(
    "adminRoleRequested",
    user.adminRoleRequested.toString()
  );
};
