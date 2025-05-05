import { Component, inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  protected auth = inject(AuthService)
  private router = inject(Router)

  logout() {
    this.auth.logout()
    this.router.navigate(['/login'])
  }
}
