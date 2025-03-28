import { Routes } from '@angular/router';
import { TableauDeBordComponent } from './components/tableau-de-bord/tableau-de-bord.component';
import { CommandesComponent } from './components/commandes/commandes.component';
import { GestionDequipesComponent } from './components/gestion-dequipes/gestion-dequipes.component';
import { LoginComponent } from './components/login/login.component';
import { TourneesComponent } from './components/tournees/tournees.component'; 
import { PlanificateurAutomatiqueComponent } from './components/planificateur-automatique/planificateur.component';
import { LivreursComponent } from './components/livreurs/livreurs.component';
import { authGuard } from '../app/app.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'tableau-de-bord', component: TableauDeBordComponent, canActivate: [authGuard]},
  { path: 'commandes', component: CommandesComponent, canActivate: [authGuard] },
  { path: 'livreurs', component: LivreursComponent, canActivate: [authGuard] },
  { path: 'gestion-dequipes', component: GestionDequipesComponent, canActivate: [authGuard] },
  { path: 'tournees', component: TourneesComponent, canActivate: [authGuard] }, 
  { path: 'planificateur-automatique', component: PlanificateurAutomatiqueComponent, canActivate: [authGuard] }, 
  { path: '', redirectTo: '/tableau-de-bord', pathMatch: 'full' }, 
  { path: '**', component: TableauDeBordComponent }
];