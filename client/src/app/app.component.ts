import { Component, inject, signal,model,computed,OnInit } from '@angular/core';
import { LatLng,latLng } from 'leaflet';
import { FormsModule } from '@angular/forms';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { OpenRouteServiceService } from './services/open-route-service.service';
import { AdresseGouvService } from './services/adresse-gouv.service';
import { RouterOutlet } from '@angular/router';
import { RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component'
import { TourneesComponent } from './components/tournees/tournees.component'
import { PlanificateurAutomatiqueComponent } from './components/planificateur-automatique/planificateur.component';
import { HeaderComponent } from './components/header/header.component';
import { TableauDeBordComponent } from './components/tableau-de-bord/tableau-de-bord.component';
import { CommandesComponent } from './components/commandes/commandes.component';
import { GestionDequipesComponent } from './components/gestion-dequipes/gestion-dequipes.component';
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

  commandes = signal<Commande[]>([]);
  equipeLivreurs = signal<EquipeLivreurs[]>([]);
  openRouteService = inject(OpenRouteServiceService);
  adresseGouvService = inject(AdresseGouvService);
  entrepot = signal<LatLng>(latLng(45.1485200, 5.7369725));

  auth = inject(Auth)
  dataService = inject(DataService);
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
