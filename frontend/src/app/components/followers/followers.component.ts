import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { UserSummary } from '../../models/user.model';
import { AuthService } from '../../services/auth.service';
import { FollowService } from '../../services/follow.service';

@Component({
  selector: 'app-followers',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './followers.component.html',
  styleUrl: './followers.component.scss'
})
export class FollowersComponent implements OnInit {
  private route = inject(ActivatedRoute);
  public router = inject(Router);
  private followService = inject(FollowService);
  private authService = inject(AuthService);

  followers: UserSummary[] = [];
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
        this.loadFollowers();
      }
    });
  }

  loadFollowers(): void {
    if (!this.userId) return;

    this.isLoading = true;
    this.followService.getFollowers(this.userId).subscribe({
      next: (followers) => {
        this.followers = followers;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }
}
