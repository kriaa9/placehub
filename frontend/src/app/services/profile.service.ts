import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { PaginatedResponse, UpdateProfileRequest, User, UserSummary } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = `${environment.apiUrl}/profile`;

  constructor(private http: HttpClient) {}

  getCurrentProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  updateProfile(request: UpdateProfileRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/me`, request);
  }

  getUserProfile(userId: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${userId}`);
  }

  searchUsers(query: string, page = 0, size = 20): Observable<UserSummary[]> {
    const params = new HttpParams()
      .set('q', query)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PaginatedResponse<UserSummary>>(`${this.apiUrl}/search`, { params })
      .pipe(map(response => response.content));
  }

  deleteAccount(password: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/me`, { body: { password } });
  }
}
