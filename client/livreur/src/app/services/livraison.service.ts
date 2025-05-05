import { computed, effect, inject, Injectable, signal } from '@angular/core';
import { LatLng } from 'leaflet';
import { Commande } from '../../models/interfaces/commande.model';
import { EtatTournee } from '../../models/enums/etat-tournee.enum';
import { EtatCommande } from '../../models/enums/etat-commande.enum';
import { DataService } from './data.service';

@Injectable({
  providedIn: 'root'
})
export class LivraisonService {
  private dataService = inject(DataService);

  commandes = signal<Commande[]>([]);
  activeIndex = signal(0);
  activeCommande = computed(() => this.commandesLeft()[this.activeIndex()]);
  started = signal(false);
  allCommandesConfirmed = signal(false);

  originalIndex = computed(() => {
    return this.activeIndex() +
           (this.commandes().length - this.commandesLeft().length);
  });

  commandesLeft = computed(() => {
    return this.commandes().filter(commande =>
      commande.etat === EtatCommande.PLANIFIEE);
  });

  constructor() {
    effect(() => {
      const t = this.dataService.tournee();
      if (t) {
        this.dataService.refreshCommandesForTournee(t.reference);
      }
    });

    effect(() => {
      const list = this.dataService.commandes();
      const t = this.dataService.tournee();
      const ordered: Commande[] = t?.commandeReferences
        ? t.commandeReferences
            .map(ref => list.find(c => c.reference === ref))
            .filter((c): c is Commande => !!c)
        : list;

      this.commandes.set(ordered);
      this.activeIndex.set(0);
      this.started.set(false);
    });

    effect(() => {
      const allConfirmed = this.commandesLeft().length === 0;
      this.allCommandesConfirmed.set(allConfirmed);
    });
  }

  startTournee() {
    const t = this.dataService.tournee();
    if (t?.etat !== EtatTournee.PLANIFIEE) return;
    this.dataService
      .updateTourneeEtat(t.reference, EtatTournee.EN_COURS)
      .subscribe(() => {
        this.dataService.refreshTournee();
        this.started.set(true);
      });
  }

  confirmLivraison() {
    const cmd = this.activeCommande();
    if (!cmd) return;
    const now = new Date().toISOString();
    this.dataService
      .updateCommande(cmd.reference, { etat: EtatCommande.LIVREE, dateDeLivraison: now })
      .subscribe(() => {
        const t = this.dataService.tournee();
        if (t) {
          this.dataService.refreshCommandesForTournee(t.reference);
        }
        this.next();
      });
  }

  signalerLivraison() {
    const cmd = this.activeCommande();

    if (!cmd) return;
    const now = new Date().toISOString();
    this.dataService
      .updateCommande(cmd.reference, { etat: EtatCommande.OUVERTE, dateDeLivraison: now })
      .subscribe(() => {
        const t = this.dataService.tournee();
        if (t) {
          this.dataService.refreshCommandesForTournee(t.reference);
        }
        this.next();
      });
  }

  private next() {
    const idx = this.activeIndex();
    if (idx < this.commandesLeft().length - 1) {
      this.activeIndex.set(idx + 1);
    } else {
      this.activeIndex.set(idx + 1);
      this.finishTournee();
    }
  }

  private finishTournee() {
    const t = this.dataService.tournee();
    if (t?.etat !== EtatTournee.EN_COURS) return;
    this.dataService
      .updateTourneeEtat(t.reference, EtatTournee.COMPLETEE)
      .subscribe(() => {
        this.dataService.refreshTournee();
        this.started.set(false);
      });
  }

  updateActiveIndex(index: number){
    this.activeIndex.set(index);
  }
}
