// Authentication models
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  firstName: string;
  lastName: string;
  password: string;
  confirmPassword: string;
}

// Response uses snake_case from backend (Jackson @JsonProperty)
export interface AuthResponse {
  access_token: string;
  refresh_token: string;
  token_type: string;
  expires_in: number;
  user_id: number;
  email: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}
