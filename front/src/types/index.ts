export interface RouteDTO {
  id?: number;
  name: string;
  coordinates: CoordinatesDTO;
  creationDate?: string;
  from: LocationDTO;
  to?: LocationDTO;
  distance?: number;
  rating: number;
  createdById?: number;
  createdByUsername?: string;
  allowAdminEditing: boolean;
}

export interface CoordinatesDTO {
  x: number;
  y: number;
}

export interface LocationDTO {
  x: number;
  y: number;
  name: string;
}

export interface AuthRequestDTO {
  username: string;
  password: string;
}

export interface AuthResponseDTO {
  token: string;
}

export interface RegisterRequestDTO {
  username: string;
  password: string;
}

export interface RegisterResponseDTO {
  id: number;
  username: string;
  roles: string[];
}

export interface UserDTO {
  id: number;
  username: string;
  roles: string[];
  adminRoleRequested: boolean;
}
