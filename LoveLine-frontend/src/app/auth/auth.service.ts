import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { BehaviorSubject, catchError, tap, throwError } from 'rxjs';
import { AuthData } from './auth-data';
import { SignUp } from './sign-up';
import { Login } from './login';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  apiURL = 'http://localhost:8080/auth/';

  jwtHelper = new JwtHelperService();
  private authSub = new BehaviorSubject<AuthData | null>(null);
  user$ = this.authSub.asObservable();
  timeout: any;

  constructor(private http: HttpClient, private router: Router) { }

  signup(data: SignUp) {

    return this.http.post(`${this.apiURL}signup`, data, {
      responseType: 'text',
    });
  }

  login(data: { email: string; password: string }) {
    const formData = new HttpParams()
      .set('email', data.email)
      .set('password', data.password);
  
    return this.http.post<AuthData>(`${this.apiURL}login_form`, formData).pipe(
      tap((data) => {
        this.authSub.next(data);
        localStorage.setItem('user', JSON.stringify(data));
      }),
      catchError((error) => {
        console.error('Login error:', error);
        return throwError(error);
      })
    );
  }

  setUser(user: AuthData | null) {
    this.authSub.next(user);
  }

  clearUser() {
    this.authSub.next(null);
    localStorage.removeItem('user');
  }

  logout() {
    this.clearUser();
    this.router.navigate(['/']);
  }

  restore() {
    const userJson = localStorage.getItem('user');
    if (!userJson) {
      return;
    } else {
      const user: AuthData = JSON.parse(userJson);
      this.authSub.next(user);
    }
  }
}
