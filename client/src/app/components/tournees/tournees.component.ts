import { Component, signal, inject, effect, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataService } from '../../services/data.service';
import { Tournee, TourneeDTO } from '../../../models/interfaces/tournee.model';
import { BackendCommunicationService } from '../../services/backendCommunication.service';
import { EquipeLivreurs } from '../../../models/interfaces/equipe-livreurs.model';
import { Commande } from '../../../models/interfaces/commande.model';
import { AdresseGouvService } from '../../services/adresse-gouv.service';

@Component({
  selector: 'app-tournees',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tournees.component.html',
  styleUrls: ['./tournees.component.scss']
})
export class TourneesComponent {

  // Modifier Not working
  
  sortColumn = signal<string>('');
  sortDirection = signal<'asc' | 'desc'>('asc');

  dataService = inject(DataService);
  adressGouvService = inject(AdresseGouvService);
  backendService = inject(BackendCommunicationService);


  constructor() {
  }

  get commandes(): Commande[] {
    return this.dataService.commandes();
  }

  get equipeLivreurs(): EquipeLivreurs[] {
    return this.dataService.equipeLivreurs();
  }


  sortedTournees = computed<Tournee[]>(() =>   {
    const items = [...this.dataService.tournees()];
    if (!this.sortColumn()) {
      return items;
    }
    items.sort((a, b) => {
      const aVal = this.getFieldValue(a, this.sortColumn());
      const bVal = this.getFieldValue(b, this.sortColumn());
      if (aVal < bVal) return this.sortDirection() === 'asc' ? -1 : 1;
      if (aVal > bVal) return this.sortDirection() === 'asc' ? 1 : -1;
      return 0;
    });
    return items;
  }
  );
  sortBy(column: string): void {
    if (this.sortColumn() === column) {
      this.sortDirection.update(dir => (dir === 'asc' ? 'desc' : 'asc'));
    } else {
      this.sortColumn.set(column);
      this.sortDirection.set('asc');
    }
  }

  getFieldValue(item: Tournee, column: string): any {
    switch (column) {
      case 'numTournee':
        return item.numTournee;
      case 'date':
        return item.date;
      case 'etat':
        return item.etat;
      case 'equipeLivreurId':
        return item.equipeLivreur;
      case 'camion':
        return item.camion;
      case 'distanceAParcourir':
        return item.distanceAParcourir ?? 0;
      default:
        return '';
    }
  }

  trackById(index: number, item: Tournee): number {
    return item.numTournee;
  }


  deleteTournee(numTournee: number): void {
    if (confirm("Êtes-vous sûr de vouloir supprimer cette tournée ?")) {
      this.backendService.deleteTournee(numTournee).subscribe({
        next: () => {
          const updatedTournees = this.dataService.tournees().filter(
            t => t.numTournee !== numTournee
          );
          this.dataService.tournees.set(updatedTournees);
        },
        error: (err) => {
          console.error("Erreur lors de la suppression de la tournée :", err);
        }
      });
    }
  }

  onClickModifier(){
    console.log("Modifier clicked", this.dataService.tournees());
  }
}