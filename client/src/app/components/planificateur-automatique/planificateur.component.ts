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
import { DataService } from '../../services/data.service';
import { MatDatepickerInputEvent, MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { BackendCommunicationService } from '../../services/backendCommunication.service';
import { Tournee } from '../../../models/interfaces/tournee.model';
import { EtatTournee } from '../../../models/enums/etat-tournee.enum';
import { from } from 'rxjs';
import { concatMap } from 'rxjs/operators';
import { KMeansService } from '../../services/kmeans.service';

@Component({
  selector: 'app-planificateur-automatique',
  standalone: true,
  imports: [
    CommonModule, MapComponent, FormsModule,
    MatFormFieldModule, MatInputModule, MatDatepickerModule, MatNativeDateModule
  ],
  templateUrl: './planificateur.component.html',
  styleUrls: ['./planificateur.component.scss']
})
export class PlanificateurAutomatiqueComponent {
  Math = Math;

  entrepot = signal<LatLng>(latLng(45.14852, 5.7369725));
  createdTourneesSignal = signal<LatLng[][]>([]);
  selectedEquipes = signal<EquipeLivreurs[]>([]);
  selectedCommandes = signal<Commande[]>([]);
  commandesOuvertes = signal<Commande[]>([]);
  selectedDate = signal<Date | null>(null);
  createdTournees = signal<Tournee[]>([]);
  nombreCommandesUtilisees = signal<number>(0);
  polylineTeamIds = signal<number[]>([]);
  polylineColorMap = signal<string[]>([]);
  teamColorMap = new Map<number, string>();
  displayedCommandes = signal<Commande[]>([]);

  openRouteService = inject(OpenRouteServiceService);
  adresseGouvService = inject(AdresseGouvService);
  leafletService = inject(LeafletService);
  backendService = inject(BackendCommunicationService);
  dataService = inject(DataService);
  kmeansService = inject(KMeansService);

  constructor() {
    const today = new Date();
    this.selectedDate.set(today);
    this.dataService.isDataLoaded().subscribe(loaded => {
      if (loaded) {
        const opened = this.dataService.commandes().filter(c => c.etat === 'OUVERTE');
        this.commandesOuvertes.set(opened);
        this.nombreCommandesUtilisees.set(opened.length);
        this.displayedCommandes.set(this.selectedCommandesAuto());
        const available = this.dataService.equipeLivreurs().filter(equipe => {
          return !equipe.dateAffecte || !equipe.dateAffecte.find(date => this.isSameDay(date, today));
        });
        this.selectedEquipes.set(available);
      }
      
    });
  }

  selectedCommandesAuto = computed(() =>
    this.commandesOuvertes().slice(0, this.nombreCommandesUtilisees())
  );

  getEquipeColor(equipeId: number): string {
    if (!this.teamColorMap.has(equipeId)) {
      const color = this.leafletService.getRandomColor();
      this.teamColorMap.set(equipeId, color);
    }
    return this.teamColorMap.get(equipeId)!;
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
      return !equipe.dateAffecte || !equipe.dateAffecte.find(date => this.isSameDay(date, selectedDay));
    });
  });

  toggleEquipe(equipe: EquipeLivreurs) {
    const current = this.selectedEquipes();
    const index = current.findIndex(e => e.numEquipe === equipe.numEquipe);
    if (index > -1) {
      this.selectedEquipes.set(current.filter((_, i) => i !== index));
    } else {
      this.selectedEquipes.set([...current, equipe]);
    }
  }

  isEquipeSelected(equipe: EquipeLivreurs): boolean {
    return this.selectedEquipes().some(e => e.numEquipe === equipe.numEquipe);
  }

  async creerTournees(): Promise<void> {
    const commandes = this.selectedCommandesAuto();
    const equipes = this.selectedEquipes();
    const entrepot = this.entrepot();
    const maxPerTeam = 50;

    if (commandes.length > equipes.length * maxPerTeam) {
      alert(`Vous avez sélectionné ${equipes.length} équipes, mais il en faut au moins ${Math.ceil(commandes.length / maxPerTeam)} pour cette distribution.`);
      return;
    }

    const coords = commandes.map(cmd => [cmd.client.adresse.latitude!, cmd.client.adresse.longitude!]);
    const clustering = this.kmeansService.kmeans(coords, equipes.length);
    const rawClusters: Commande[][] = Array.from({ length: equipes.length }, () => []);

    commandes.forEach((cmd, idx) => {
      const clusterId = clustering.clusters[idx];
      rawClusters[clusterId].push(cmd);
    });

    const tournees: Partial<Tournee>[] = [];
    const cheminsOptimise: LatLng[][] = [];
    const teamIds: number[] = [];

    for (let i = 0; i < rawClusters.length; i++) {
      const clusterCommandes = rawClusters[i];
      const equipe = equipes[i];
      if (clusterCommandes.length === 0) continue;

      const response = await this.openRouteService.getOptimizationAutmatique(clusterCommandes, [equipe], entrepot);
      if (!response?.routes?.length) continue;

      const route = response.routes[0];
      const cheminOptimise: LatLng[] = [];
      const commandesOrderById: number[] = [];

      for (let step of route.steps) {
        if (step.id !== undefined) commandesOrderById.push(step.id);
        cheminOptimise.push(new LatLng(step.location[1], step.location[0]));
      }

      tournees.push({
        date: this.selectedDate() || new Date(),
        equipeLivreur: equipe,
        commandes: commandesOrderById.map(id => commandes.find(c => c.reference === id)).filter((c): c is Commande => !!c),
        etat: EtatTournee.EN_ATTENTE
      });

      cheminsOptimise.push(cheminOptimise);
      teamIds.push(equipe.numEquipe);
      this.polylineColorMap.set([...this.polylineColorMap(), this.getEquipeColor(equipe.numEquipe)]);

      if (!equipe.dateAffecte) equipe.dateAffecte = [];
      equipe.dateAffecte.push(this.selectedDate() || new Date());
    }

    this.createdTourneesSignal.set(cheminsOptimise);
    this.polylineTeamIds.set(teamIds);
    this.createdTournees.set([...this.createdTournees(), ...tournees as Tournee[]]);

    const usedCommandeIds = new Set<number>();
    tournees.forEach(t => t.commandes?.forEach(c => usedCommandeIds.add(c.reference)));
    this.commandesOuvertes.set(this.commandesOuvertes().filter(c => !usedCommandeIds.has(c.reference)));

    from(tournees).pipe(
      concatMap(tournee => {
        const tourneeDTO = {
          date: tournee.date instanceof Date ? tournee.date.toISOString() : tournee.date,
          etat: tournee.etat!,
          equipeLivreurId: tournee.equipeLivreur?.numEquipe || 0,
          commandesIds: tournee.commandes!.map(c => c.reference)
        };
        return this.backendService.postTourneeN(tourneeDTO);
      })
    ).subscribe();

    this.dataService.updateTournees();
    this.displayedCommandes.set(this.selectedCommandesAuto());
  }

  actualiserCommandes() {
    this.displayedCommandes.set(this.selectedCommandesAuto());
  }

  onDateChange(event: MatDatepickerInputEvent<Date>) {
    this.selectedDate.set(event.value);
    this.selectedCommandes.set([]);
  }

  latLngTable = signal<LatLng[]>([]);
}