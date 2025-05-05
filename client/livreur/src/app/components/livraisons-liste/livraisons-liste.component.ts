import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LivraisonService } from '../../services/livraison.service';


@Component({
  selector: 'app-livraisons-liste',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './livraisons-liste.component.html',
  styleUrls: ['./livraisons-liste.component.scss'],
})
export class LivraisonsListeComponent {

  livraisonService = inject(LivraisonService);

  readonly commandes = this.livraisonService.commandes;
  readonly originalIndex = this.livraisonService.originalIndex;
  readonly allCommandesConfirmed = this.livraisonService.allCommandesConfirmed;
}
