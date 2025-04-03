import { Component, Input, Output, EventEmitter, signal } from '@angular/core';
import { CdkDragDrop, DragDropModule, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { EquipeLivreurs } from '../../../../models/interfaces/equipe-livreurs.model';
import { Commande } from '../../../../models/interfaces/commande.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';


@Component({
    standalone: true,
    imports: [CommonModule, DragDropModule, FormsModule, MatFormFieldModule, MatInputModule],
    selector: 'app-drag-drop-selector',
  templateUrl: './drag-drop-selector.component.html',
  styleUrls: ['./drag-drop-selector.component.scss']
})
export class DragDropSelectorComponent {
  @Input() availableLivreurs: EquipeLivreurs[] = [];
  @Input() availableCommandes: Commande[] = [];
  
  // Selected items that the user drops here
  selectedLivreurs: EquipeLivreurs[] = [];
  selectedCommandes: Commande[] = [];
  
  @Output() selectionChange = new EventEmitter<{
    livreurs: EquipeLivreurs[],
    commandes: Commande[]
  }>();

  dropLivreurs(event: CdkDragDrop<EquipeLivreurs[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      // When moving from available to selected (or vice versa)
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
    }
    this.emitSelectionChange();
  }

  dropCommandes(event: CdkDragDrop<Commande[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
    }
    this.emitSelectionChange();
  }

  emitSelectionChange() {
    this.selectionChange.emit({
      livreurs: this.selectedLivreurs,
      commandes: this.selectedCommandes
    });
  }


  searchText = signal<string>('');
  
  get filteredAvailableCommandes() {
    return this.availableCommandes.filter(commande => 
      commande.reference.toString().includes(this.searchText()) ||
      commande.client.nom.toLowerCase().includes(this.searchText().toLowerCase()) ||
      commande.client.prenom.toLowerCase().includes(this.searchText().toLowerCase())
    );
  }

}
