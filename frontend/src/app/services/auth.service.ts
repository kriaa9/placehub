import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';
import { User } from '../models/user.model';

/**
 * Authentication Service
 * Handles user authentication, token management, and session persistence.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;
  private readonly TOKEN_KEY = 'accessToken';
  private readonly REFRESH_TOKEN_KEY = 'refreshToken';
  private readonly USER_ID_KEY = 'userId';

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasValidToken());

  /** Observable of current user */
  currentUser$ = this.currentUserSubject.asObservable();

  /** Observable of authentication state */
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    // Load user on service initialization if token exists
    this.initializeAuth();
  }

  /**
   * Initialize authentication state on app load
   */
  private initializeAuth(): void {
    if (this.hasValidToken()) {
      this.loadCurrentUser();
    }
  }

  /**
   * Check if a valid token exists in storage
   */
  private hasValidToken(): boolean {
    const token = localStorage.getItem(this.TOKEN_KEY);
    return !!token && token.length > 0;
  }

  /**
   * Load current user profile from API
   */
  private loadCurrentUser(): void {
    this.http.get<User>(`${environment.apiUrl}/profile/me`).subscribe({
      next: (user) => {
        this.currentUserSubject.next(user);
        this.isAuthenticatedSubject.next(true);
      },
      error: () => {
        // Token is invalid, clear auth state
        this.clearAuthData();
      }
    });
  }

  /**
   * Register a new user
   */
  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap(response => this.handleAuthSuccess(response)),
      catchError(error => throwError(() => error))
    );
  }

  /**
   * Login with email and password
   */
  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => this.handleAuthSuccess(response)),
      catchError(error => throwError(() => error))
    );
  }

  /**
   * Logout current user
   */
  logout(): void {
    this.clearAuthData();
    this.router.navigate(['/login']);
  }

  /**
   * Refresh access token using refresh token
   */
  refreshToken(): Observable<AuthResponse> {
    const refreshToken = localStorage.getItem(this.REFRESH_TOKEN_KEY);

    if (!refreshToken) {
      this.clearAuthData();
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
      tap(response => this.handleAuthSuccess(response)),
      catchError(error => {
        this.clearAuthData();
        return throwError(() => error);
      })
    );
  }

  /**
   * Get current access token
   */
  getAccessToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return this.hasValidToken();
  }

  /**
   * Get current user value (synchronous)
   */
  getCurrentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Handle successful authentication response
   */
  private handleAuthSuccess(response: AuthResponse): void {
    // Store tokens
    localStorage.setItem(this.TOKEN_KEY, response.access_token);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, response.refresh_token);
    localStorage.setItem(this.USER_ID_KEY, response.user_id.toString());

    // Update auth state
    this.isAuthenticatedSubject.next(true);

    // Load user profile
    this.loadCurrentUser();
  }

  /**
   * Clear all authentication data
   */
  private clearAuthData(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_ID_KEY);
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
  }
}
