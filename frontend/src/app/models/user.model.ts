// User model - matches backend UserProfileResponse
export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  bio?: string;
  avatarUrl?: string;
  phone?: string;        // Backend uses 'phone', not 'phoneNumber'
  url?: string;
  city?: string;
  country?: string;
  address?: string;
  homeLatitude?: number;
  homeLongitude?: number;
  instagram?: string;
  facebook?: string;
  linkedin?: string;
  tiktok?: string;
  followersCount: number;
  followingCount: number;
  placesCount: number;
  listsCount: number;
  createdAt: string;
  updatedAt?: string;
  isFollowing?: boolean;
}

export interface UserSummary {
  id: number;
  firstName: string;
  lastName: string;
  avatarUrl?: string;
  bio?: string;
  city?: string;
  country?: string;
  followersCount: number;
  followingCount: number;
  publicListsCount: number;
  placesCount?: number;
  isFollowing: boolean;
}

// Matches backend UpdateProfileRequest
export interface UpdateProfileRequest {
  firstName?: string;
  lastName?: string;
  bio?: string;
  avatarUrl?: string;
  phone?: string;        // Backend uses 'phone'
  url?: string;
  city?: string;
  country?: string;
  address?: string;
  homeLatitude?: number;
  homeLongitude?: number;
  instagram?: string;
  facebook?: string;
  linkedin?: string;
  tiktok?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
