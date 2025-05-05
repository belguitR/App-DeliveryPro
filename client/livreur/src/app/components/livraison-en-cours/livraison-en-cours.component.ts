import { Component, inject } from '@angular/core';
import { LivraisonService } from '../../services/livraison.service';

import { DataService } from '../../services/data.service';

@Component({
  selector: 'app-livraison-en-cours',
  standalone: true,
  imports: [],
  templateUrl: './livraison-en-cours.component.html',
  styleUrl: './livraison-en-cours.component.scss'
})
export class LivraisonEnCoursComponent {

  livraisonService = inject(LivraisonService);
  dataService = inject(DataService);

  readonly commandesLeft = this.livraisonService.commandesLeft;
  readonly tournee = this.dataService.tournee;
  readonly index = this.livraisonService.activeIndex;

  readonly activeCommande = this.livraisonService.activeCommande;

  onStart(){
    this.livraisonService.startTournee();
  }

  onConfirm() {
    console.log("onConfirm called in component");
    this.livraisonService.confirmLivraison();
  }
  onSignal(){
    this.livraisonService.signalerLivraison();
  }

}
