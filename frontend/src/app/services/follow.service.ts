import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { PaginatedResponse, UserSummary } from '../models/user.model';

export interface FollowStatusResponse {
  isFollowing: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class FollowService {
  private apiUrl = `${environment.apiUrl}/follow`;

  constructor(private http: HttpClient) {}

  followUser(userId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${userId}`, {});
  }

  unfollowUser(userId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${userId}`);
  }

  getFollowers(userId: number, page = 0, size = 20): Observable<UserSummary[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PaginatedResponse<UserSummary>>(`${this.apiUrl}/followers/${userId}`, { params })
      .pipe(map(response => response.content));
  }

  getFollowing(userId: number, page = 0, size = 20): Observable<UserSummary[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PaginatedResponse<UserSummary>>(`${this.apiUrl}/following/${userId}`, { params })
      .pipe(map(response => response.content));
  }

  isFollowing(userId: number): Observable<FollowStatusResponse> {
    return this.http.get<FollowStatusResponse>(`${this.apiUrl}/status/${userId}`);
  }
}
