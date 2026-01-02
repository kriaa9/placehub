import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { User } from '../../models/user.model';
import { AuthService } from '../../services/auth.service';
import { FollowService } from '../../services/follow.service';
import { ProfileService } from '../../services/profile.service';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss'
})
export class UserProfileComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private profileService = inject(ProfileService);
  private followService = inject(FollowService);
  private authService = inject(AuthService);

  user: User | null = null;
  isLoading = true;
  errorMessage = '';
  isFollowing = false;
  isOwnProfile = false;
  followersCount = 0;
  followingCount = 0;
  isFollowLoading = false;

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const userId = +params['id'];
      this.loadUserProfile(userId);
    });
  }

  loadUserProfile(userId: number): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Check if this is the current user's profile
    this.authService.currentUser$.subscribe(currentUser => {
      if (currentUser && currentUser.id === userId) {
        this.isOwnProfile = true;
        this.router.navigate(['/profile']);
        return;
      }
    });

    this.profileService.getUserProfile(userId).subscribe({
      next: (user) => {
        this.user = user;
        this.loadFollowStatus(userId);
        this.loadFollowCounts(userId);
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.status === 404
          ? 'User not found.'
          : 'Failed to load user profile.';
        this.isLoading = false;
      }
    });
  }

  loadFollowStatus(userId: number): void {
    this.followService.isFollowing(userId).subscribe({
      next: (status) => this.isFollowing = status.isFollowing
    });
  }

  loadFollowCounts(userId: number): void {
    this.followService.getFollowers(userId).subscribe({
      next: (followers) => this.followersCount = followers.length
    });
    this.followService.getFollowing(userId).subscribe({
      next: (following) => this.followingCount = following.length
    });
  }

  toggleFollow(): void {
    if (!this.user) return;

    this.isFollowLoading = true;

    if (this.isFollowing) {
      this.followService.unfollowUser(this.user.id).subscribe({
        next: () => {
          this.isFollowing = false;
          this.followersCount--;
          this.isFollowLoading = false;
        },
        error: () => {
          this.isFollowLoading = false;
        }
      });
    } else {
      this.followService.followUser(this.user.id).subscribe({
        next: () => {
          this.isFollowing = true;
          this.followersCount++;
          this.isFollowLoading = false;
        },
        error: () => {
          this.isFollowLoading = false;
        }
      });
    }
  }
}
