import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { debounceTime, Subject } from 'rxjs';
import { UserSummary } from '../../models/user.model';
import { ProfileService } from '../../services/profile.service';

@Component({
  selector: 'app-user-search',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './user-search.component.html',
  styleUrl: './user-search.component.scss'
})
export class UserSearchComponent {
  private profileService = inject(ProfileService);
  private router = inject(Router);

  searchQuery = '';
  users: UserSummary[] = [];
  isLoading = false;
  hasSearched = false;
  private searchSubject = new Subject<string>();

  constructor() {
    this.searchSubject.pipe(debounceTime(300)).subscribe(query => {
      this.performSearch(query);
    });
  }

  onSearchChange(): void {
    if (this.searchQuery.length >= 2) {
      this.searchSubject.next(this.searchQuery);
    } else {
      this.users = [];
      this.hasSearched = false;
    }
  }

  performSearch(query: string): void {
    this.isLoading = true;
    this.profileService.searchUsers(query).subscribe({
      next: (users) => {
        this.users = users;
        this.isLoading = false;
        this.hasSearched = true;
      },
      error: () => {
        this.isLoading = false;
        this.hasSearched = true;
      }
    });
  }

  viewProfile(userId: number): void {
    this.router.navigate(['/user', userId]);
  }
}
