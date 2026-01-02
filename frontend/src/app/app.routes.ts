import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent),
    canActivate: [guestGuard]
  },
  {
    path: 'register',
    loadComponent: () => import('./components/register/register.component').then(m => m.RegisterComponent),
    canActivate: [guestGuard]
  },
  {
    path: 'profile',
    loadComponent: () => import('./components/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [authGuard]
  },
  {
    path: 'profile/followers',
    loadComponent: () => import('./components/followers/followers.component').then(m => m.FollowersComponent),
    canActivate: [authGuard]
  },
  {
    path: 'profile/following',
    loadComponent: () => import('./components/following/following.component').then(m => m.FollowingComponent),
    canActivate: [authGuard]
  },
  {
    path: 'search',
    loadComponent: () => import('./components/user-search/user-search.component').then(m => m.UserSearchComponent),
    canActivate: [authGuard]
  },
  {
    path: 'user/:id',
    loadComponent: () => import('./components/user-profile/user-profile.component').then(m => m.UserProfileComponent),
    canActivate: [authGuard]
  },
  {
    path: 'user/:id/followers',
    loadComponent: () => import('./components/followers/followers.component').then(m => m.FollowersComponent),
    canActivate: [authGuard]
  },
  {
    path: 'user/:id/following',
    loadComponent: () => import('./components/following/following.component').then(m => m.FollowingComponent),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];
