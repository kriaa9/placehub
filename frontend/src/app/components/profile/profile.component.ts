import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { User } from '../../models/user.model';
import { FollowService } from '../../services/follow.service';
import { ProfileService } from '../../services/profile.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  private fb = inject(FormBuilder);
  private profileService = inject(ProfileService);
  private followService = inject(FollowService);
  private router = inject(Router);

  user: User | null = null;
  profileForm: FormGroup;
  isLoading = true;
  isEditing = false;
  isSaving = false;
  errorMessage = '';
  successMessage = '';
  followersCount = 0;
  followingCount = 0;

  constructor() {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      bio: ['', [Validators.maxLength(500)]],
      phone: ['', [Validators.pattern(/^\+?[0-9]{7,20}$/)]],
      address: ['', [Validators.maxLength(255)]],
      city: ['', [Validators.maxLength(100)]],
      country: ['', [Validators.maxLength(100)]],
      homeLatitude: ['', [Validators.min(-90), Validators.max(90)]],
      homeLongitude: ['', [Validators.min(-180), Validators.max(180)]],
      instagram: [''],
      facebook: [''],
      linkedin: [''],
      tiktok: ['']
    });
  }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.isLoading = true;
    this.profileService.getCurrentProfile().subscribe({
      next: (user) => {
        this.user = user;
        this.patchFormValues(user);
        this.loadFollowCounts(user.id);
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load profile. Please try again.';
        this.isLoading = false;
      }
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

  patchFormValues(user: User): void {
    this.profileForm.patchValue({
      firstName: user.firstName,
      lastName: user.lastName,
      bio: user.bio || '',
      phone: user.phone || '',
      address: user.address || '',
      city: user.city || '',
      country: user.country || '',
      homeLatitude: user.homeLatitude || '',
      homeLongitude: user.homeLongitude || '',
      instagram: user.instagram || '',
      facebook: user.facebook || '',
      linkedin: user.linkedin || '',
      tiktok: user.tiktok || ''
    });
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    if (!this.isEditing && this.user) {
      this.patchFormValues(this.user);
    }
    this.errorMessage = '';
    this.successMessage = '';
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.profileService.updateProfile(this.profileForm.value).subscribe({
      next: (updatedUser) => {
        this.user = updatedUser;
        this.isEditing = false;
        this.isSaving = false;
        this.successMessage = 'Profile updated successfully!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.isSaving = false;
        this.errorMessage = error.error?.message || 'Failed to update profile.';
      }
    });
  }
}
