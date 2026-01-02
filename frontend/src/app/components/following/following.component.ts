import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { UserSummary } from '../../models/user.model';
import { AuthService } from '../../services/auth.service';
import { FollowService } from '../../services/follow.service';

@Component({
  selector: 'app-following',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './following.component.html',
  styleUrl: './following.component.scss'
})
export class FollowingComponent implements OnInit {
  private route = inject(ActivatedRoute);
  public router = inject(Router);
  private followService = inject(FollowService);
  private authService = inject(AuthService);

  following: UserSummary[] = [];
  isLoading = true;
  userId: number | null = null;
  isOwnProfile = true;

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.userId = +params['id'];
        this.isOwnProfile = false;
      } else {
        this.authService.currentUser$.subscribe(user => {
          if (user) {
            this.userId = user.id;
          }
        });
      }
      if (this.userId) {
        this.loadFollowing();
      }
    });
  }

  loadFollowing(): void {
    if (!this.userId) return;

    this.isLoading = true;
    this.followService.getFollowing(this.userId).subscribe({
      next: (following) => {
        this.following = following;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }
}
