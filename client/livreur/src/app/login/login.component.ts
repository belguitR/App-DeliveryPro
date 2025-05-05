import { Component, inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
})
export class LoginComponent {
  email: string = ''
  password: string = ''

  private auth = inject(AuthService)
  private router = inject(Router)
  errorMessage: string | null = null

  onSubmit(): void {
    this.auth.login(this.email, this.password)
    .subscribe({
      next: () => {
        this.router.navigate(['/dashboard']); 
      },
      error: (err) => {
        this.errorMessage = err.code
      }
    })
    console.log(this.auth.username())
  }
}