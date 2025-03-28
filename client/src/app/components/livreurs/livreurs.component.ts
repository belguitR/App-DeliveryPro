import { Component, inject, model, signal , OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Livreur } from '../../../models/interfaces/livreur.model';
import { BackendCommunicationService } from '../../services/backendCommunication.service';
import { DataService } from '../../services/data.service';

@Component({
  selector: 'app-livreurs',
  imports: [CommonModule, FormsModule],
  templateUrl: './livreurs.component.html',
  styleUrls: ['./livreurs.component.scss']
})
export class LivreursComponent {


  backendService = inject(BackendCommunicationService);
  dataService = inject(DataService);
  livreurs = model.required<Livreur[]>();
  showModal = signal(false);
  editingLivreurId = signal<string | null>(null);
  errorMessage = signal<string | null>(null);


  private getDefaultLivreurForm(): Livreur {
    return {
      nom: '',
      prenom: '',
      telephone: '',
      photoURL: '',
      apermis: true,
      email: '',
      affecte: false,
      entrepot: {
        nom: 'entroptTest',
      },
      idEmploye: '',
      dateNaissance: new Date("1970-01-01")
    };
  }

  livreurForm = model<Livreur>(this.getDefaultLivreurForm());

  constructor() {
    this.dataService.isDataLoaded().subscribe(loaded => {
      if (loaded) {
    this.livreurs.set(this.dataService.livreurs());      }
  });
  }


  private resetLivreurForm(): void {
    this.livreurForm.set(this.getDefaultLivreurForm());
  }

  private getLivreurFromForm(id?: string): Livreur {

    const defaultPhoto = 'https://static.vecteezy.com/system/resources/thumbnails/001/840/618/small_2x/picture-profile-icon-male-icon-human-or-people-sign-and-symbol-free-vector.jpg';
    return {
      idEmploye: id ? id : this.genererNouvelId(),
      nom: this.livreurForm().nom,
      prenom: this.livreurForm().prenom,
      telephone: this.livreurForm().telephone,
      photoURL: this.livreurForm().photoURL || defaultPhoto,
      apermis: this.livreurForm().apermis,
      email: this.livreurForm().email,
      entrepot: this.livreurForm().entrepot as any,
      dateNaissance: this.livreurForm().dateNaissance,
      affecte: this.livreurForm().affecte
    };
  }

  creerLivreur(): void {
    const nouveauLivreur = this.getLivreurFromForm();
    this.backendService.postLivreur(nouveauLivreur).subscribe({
      next: (res: any) => {
        if (res) {
          console.log("Livreur posté avec succès :", res);
          const livreurToAdd = res.data || nouveauLivreur;
          this.livreurs.update(current => [...current, livreurToAdd]);
          this.showModal.set(false);
          this.resetLivreurForm();
          this.errorMessage.set(null); 
        } else {
          console.error("Erreur dans la réponse du POST :", res);
        }
      },
      error: (err) => {
        console.error("Erreur lors de l'envoi du livreur :", err);
        this.errorMessage.set(err.error?.message || "Une erreur est survenue.");
      }
    });
  }

  modifierLivreur(id: string): void {
    const livreur = this.livreurs().find(l => l.idEmploye === id);
    if (livreur) {
      this.livreurForm.set({
        nom: livreur.nom,
        prenom: livreur.prenom,
        telephone: livreur.telephone,
        photoURL: livreur.photoURL || '',
        apermis: livreur.apermis,
        email: livreur.email,
        entrepot: livreur.entrepot,
        idEmploye: livreur.idEmploye,
        dateNaissance: livreur.dateNaissance,
        affecte: livreur.affecte
      });
      this.editingLivreurId.set(id);
      this.showModal.set(true);
    }
  }

  enregistrerModification(): void {
    const id = this.editingLivreurId();
    if (id) {
      const livreurModifie = this.getLivreurFromForm(id);
      this.backendService.putLivreur(livreurModifie).subscribe({
        next: (res: any) => {
          console.log("Livreur modifié avec succès :", res); 
          this.livreurs.update(old => old.map(l => l.idEmploye === id ? livreurModifie : l));
          this.errorMessage.set(null);
        },
        error: (err) => {
          console.error("Erreur lors de l'envoi du livreur :", err);
          const errorMsg = (err.error && err.error.message) ? err.error.message : "Une erreur est survenue.";
          this.errorMessage.set(errorMsg);
        }
      });
      console.log("Livreur modifié :", livreurModifie);
      this.showModal.set(false);
      this.editingLivreurId.set(null);
      this.resetLivreurForm();
    }
  }

  annulerCreation(): void {
    this.showModal.set(false);
    this.editingLivreurId.set(null);
    this.errorMessage.set(null);
    this.resetLivreurForm();
  }

  genererNouvelId(): string {
    let dernier = 0;
    this.livreurs().forEach(livreur => {
      const num = livreur.idEmploye ? parseInt(livreur.idEmploye.replace(/[^\d]/g, ''), 10) : 0;
      if (num > dernier) {
        dernier = num;
      }
    });
    return 'jdd' + (dernier + 1);
  }
}
