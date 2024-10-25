import api from "./axios";
import {
  AuthRequestDTO,
  AuthResponseDTO,
  RegisterRequestDTO,
  RegisterResponseDTO,
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
  return response.data;
};
