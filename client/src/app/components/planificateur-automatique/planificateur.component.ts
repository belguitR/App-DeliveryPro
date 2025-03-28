import { Component, model, signal, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MapComponent } from '../map/map.component';
import { EquipeLivreurs } from '../../../models/interfaces/equipe-livreurs.model';
import { Commande } from '../../../models/interfaces/commande.model';
import { FormsModule } from '@angular/forms';
import { OpenRouteServiceService } from '../../services/open-route-service.service';
import { AdresseGouvService } from '../../services/adresse-gouv.service';
import { LeafletService } from '../../services/leaflet.service';
import { LatLng, latLng } from 'leaflet';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { DataService } from '../../services/data.service';
import { MatDatepickerInputEvent, MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { DragDropSelectorComponent } from './drag-drop-selector/drag-drop-selector.component';
import { BackendCommunicationService } from '../../services/backendCommunication.service';
import { Tournee, TourneeDTO } from '../../../models/interfaces/tournee.model';
import { EtatTournee } from '../../../models/enums/etat-tournee.enum';
import { from } from 'rxjs';
import { concatMap } from 'rxjs/operators';


@Component({
  selector: 'app-planificateur-automatique',
  standalone: true,
  imports: [CommonModule, MapComponent, FormsModule, DragDropModule, MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    DragDropSelectorComponent
  ],
  templateUrl: './planificateur.component.html',
  styleUrls: ['./planificateur.component.scss']
})
export class PlanificateurAutomatiqueComponent {


  entrepot = signal<LatLng>(latLng(45.1485200, 5.7369725));
  destinationsGeoCode = signal<LatLng[]>([]);
  itenairaire = signal<any[]>([]);
  cheminOptimiseSignal = signal<LatLng[]>([]);
  createdTourneesSignal = signal<LatLng[][]>([]);

  selectedEquipes = signal<EquipeLivreurs[]>([]);
  selectedCommandes = signal<Commande[]>([]);
  commandesOuvertes = signal<Commande[]>([]);

  selectedDate = signal<Date | null>(null);
  createdTournees = signal<Tournee[]>([]);



  openRouteService = inject(OpenRouteServiceService);
  adresseGouvService = inject(AdresseGouvService);
  leafletService = inject(LeafletService);
  backendService = inject(BackendCommunicationService);
  dataService = inject(DataService);

  constructor() {
    this.dataService.isDataLoaded().subscribe(loaded => {
      if (loaded) {
    this.commandesOuvertes.set(this.dataService.commandes().filter(commande => commande.etat === 'OUVERTE'));
    }
  });
  }

  
  private isSameDay(d1: Date, d2: Date): boolean {
    return d1.getFullYear() === d2.getFullYear() &&
           d1.getMonth() === d2.getMonth() &&
           d1.getDate() === d2.getDate();
  }
  availableEquipeLivreurs = computed(() => {
    const selectedDay = this.selectedDate();
    if (!selectedDay) return [];
    
    return this.dataService.equipeLivreurs().filter(equipe => {
      if (!equipe.dateAffecte) return true;
      const booked = equipe.dateAffecte.find(date => this.isSameDay(date, selectedDay));
      return booked === undefined;
    });
});


      

  handleSelectionChange(selection: { livreurs: EquipeLivreurs[], commandes: Commande[] }) {

    this.selectedEquipes.set(selection.livreurs);
    this.selectedCommandes.set(selection.commandes);
  }

  async creerTournees(): Promise<void> {

    let tournees: Partial<Tournee>[] = [];

    const optimizationResponse = await this.openRouteService.getOptimizationAutmatique(this.selectedCommandes(),this.selectedEquipes(),this.entrepot());
    console.log('optimizationResponse', optimizationResponse);

    const optimizedRoutes = optimizationResponse.routes;  
  
    let cheminsOptimise: LatLng[][] = [];
    let numTournee = 20;
    let j = 0;
    for(let route of optimizedRoutes){
      let cheminOptimise: LatLng[] = [];

      let commandesOrderById: number[] = [];

      for (let i = 0; i < route.steps.length; i++) {
      const location = route.steps[i].location;
      let commandeId = 0;
      if (route.steps[i].id !== undefined){
        commandeId = route.steps[i].id;
        commandesOrderById.push(commandeId);

      }
      cheminOptimise.push(new LatLng(location[1], location[0]));


      
    }
    console.log('commandesOrderById',j,commandesOrderById);
    const tournee: Partial<Tournee> = {
      date: this.selectedDate() || new Date(),
      equipeLivreur: this.selectedEquipes()[j],
      commandes: commandesOrderById.map(commandeId =>
        this.selectedCommandes().find(commande => commande.reference === commandeId)
      ).filter((commande): commande is Commande => commande !== undefined),
      etat: EtatTournee.EN_ATTENTE
    };
    tournees.push(tournee);
    cheminsOptimise.push(cheminOptimise);
    console.log('selected equipe',j, this.selectedEquipes()[j]);

    if (tournee.equipeLivreur) {
      if (!tournee.equipeLivreur.dateAffecte) {
        tournee.equipeLivreur.dateAffecte = [];
      }
      tournee.equipeLivreur.dateAffecte.push(this.selectedDate() || new Date());
    }
    j++;
  }
  this.createdTourneesSignal.set(cheminsOptimise);
  this.createdTournees.set([
    ...this.createdTournees(),
    ...tournees as Tournee[]
  ]);
  console.log('tournees', tournees);

//   for (let tournee of tournees) {
//     const tourneeDTO: Omit<TourneeDTO, 'numTournee'> = {
//     date: tournee.date!,
//     etat: tournee.etat!,
//     equipeLivreurId: tournee.equipeLivreur?.numEquipe || 0,
//     commandesIds: tournee.commandes!.map(commande => commande.reference),
// };
//     this.backendService.postTournee(tourneeDTO).subscribe({
//       next: (response: any) => {
//         const newTournee = {
//           numTournee: response.numTournee,
//           date: tournee.date!,
//           etat: tournee.etat!,
//           equipeLivreur: tournee.equipeLivreur!,
//           commandes: tournee.commandes!,
//           distanceAParcourir: response.distanceAParcourir,
//           montant: response.montant,
//           tempsDeMontageTheorique: response.tempsDeMontageTheorique,
//           tempsDeMontageEffectif: response.tempsDeMontageEffectif,
//           camion: response.camion,
//           planificateur: response.planificateur
//         };
//         this.dataService.tournees.update(current => [...current, newTournee]);
//         console.log('Tournee created and signal updated:', newTournee);
//       },
//       error: (err) => {
//         console.error('Error creating tournee:', err);
//       }
//     });
//   }

from(tournees).pipe(
  concatMap(tournee => {
    const tourneeNDTO = {
      date: (tournee.date instanceof Date ? tournee.date.toISOString() : tournee.date),
      etat: tournee.etat!,
      equipeLivreurId: tournee.equipeLivreur ? tournee.equipeLivreur.numEquipe : 0,
      commandesIds: tournee.commandes!.map(commande => commande.reference),
    };
    console.log('Posting tourneeNDTO', tourneeNDTO);
    return this.backendService.postTourneeN(tourneeNDTO);
  })
).subscribe({
  next: (response: any) => {
    console.log('TourneeN response', response);
  },
  error: (err) => {
    console.error('Error creating tournee:', err);
  }
});

  this.dataService.updateTournees();

}
onDateChange(event: MatDatepickerInputEvent<Date>) {
  this.selectedDate.set(event.value);
  this.selectedEquipes.set([]);
  this.selectedCommandes.set([]);
}


  
  

}