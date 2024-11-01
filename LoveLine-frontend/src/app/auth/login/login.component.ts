import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { Login } from '../login';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  errorMessage: string = '';
  jwtHelper: any;

  ngOnInit(): void {
    this.iconsMenuAnimations();
  }

constructor(private authSrv: AuthService, private router: Router) { }

  login(form: NgForm) {
    const login: Login = form.value;
    this.authSrv.login(login).subscribe(
      () => {
        this.router.navigate(['/home']);
      },
      (error: HttpErrorResponse) => {
        console.error('Error logging in:', error);
        let errorMessage = 'An error occurred during login.';
        
        if (error.status === 403) {
          errorMessage = 'Access denied. Please check your credentials.';
        } else if (error.error && typeof error.error === 'object') {
          errorMessage = 'Server error. Please try again later.';
        } else if (error.error && typeof error.error === 'string') {
          errorMessage = error.error;
        }
        
        this.errorMessage = errorMessage;
      }
    );
  }

  
  
  iconsMenuAnimations(): void {
    document.querySelectorAll('a').forEach(function (link) {
      const svg = link.querySelector('svg');

      link.addEventListener('mouseenter', function () {
        gsap.to(svg, {
          duration: 0.1,
          opacity: 1,
          scale: 1.1,
          ease: 'power4.out',
        });
      });

      link.addEventListener('mouseleave', function () {
        gsap.to(svg, {
          duration: 0.1,
          opacity: 0,
          scale: 0.5,
          ease: 'power4.in',
        });
      });
    });
  }
}
