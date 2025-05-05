import { Component, inject, signal,model,computed,OnInit } from '@angular/core';
import { LatLng,latLng } from 'leaflet';
import { FormsModule } from '@angular/forms';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { RouterOutlet } from '@angular/router';
import { RouterModule } from '@angular/router';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';


import { EquipeLivreurs } from '../models/interfaces/equipe-livreurs.model';
import { Commande } from '../models/interfaces/commande.model';
import { DataService } from './services/data.service';
import { Auth, onAuthStateChanged } from '@angular/fire/auth';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    FormsModule,    
    RouterModule,
    MatSlideToggleModule,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {


  auth = inject(Auth)
  authService = inject(AuthService)
  router = inject(Router)

  showNavbar = false;

  constructor() {
    onAuthStateChanged(this.auth, (user) => {
      this.showNavbar = !!user;
      if (user) {
        this.router.navigate(['/tableau-de-bord']); 
      } else {
        this.router.navigate(['/login']);
      }
    });
  }

  logout() {
    this.authService.logout()
    this.router.navigate(['/login'])
  }
}
