import { Component, inject, TemplateRef, ViewChild } from '@angular/core';
import { LivraisonService } from '../../services/livraison.service';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { DataService } from '../../services/data.service';

@Component({
  selector: 'app-livraison-en-cours',
  standalone: true,
  imports: [MatDialogModule],
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

  private dialog = inject(MatDialog);

  @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
  dialogRef!: MatDialogRef<any>;
  
  onStart() {
    this.livraisonService.startTournee();
  }

  onConfirm() {
    this.dialogRef = this.dialog.open(this.confirmDialog, {
      data: {
        title: 'Confirmer la livraison',
        message: 'Voulez-vous  confirmer cette livraison ?',
      },
    });

    this.dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.livraisonService.confirmLivraison();
      }
    });
  }

  onSignal() {
    this.dialogRef = this.dialog.open(this.confirmDialog, {
      data: {
        title: 'Signaler la livraison',
        message: 'Voulez-vous signaler cette livraison ?',
      },
    });

    this.dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.livraisonService.signalerLivraison();
      }
    });
  }

}
