import { Injectable } from '@angular/core';
import { Livreur } from '../../models/interfaces/livreur.model';
import { catchError, Observable, throwError } from 'rxjs';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { EquipeLivreurs } from '../../models/interfaces/schemas/equipe-livreurs.schema';
import { StatusEquipeLivreurs } from '../../models/enums/status-equipe-livreurs.enum';
import { Commande } from '../../models/interfaces/commande.model';
import { Tournee } from '../../models/interfaces/tournee.model';
import { TourneeDTO } from '../../models/interfaces/tournee.model';

export interface EquipeLivreursDTO {
  numEquipe: number;
  status: string;
  livreursIds: string[];
}

export interface CreateEquipeLivreursDTO {
  numEquipe: number;
  status: string;
  livreurs: Livreur[];
}

@Injectable({
  providedIn: 'root'
})
export class BackendCommunicationService {

  constructor(private http: HttpClient) { }

  private siteLink = "https://main-bvxea6i-cblr3dirpybii.eu-5.platformsh.site/"

  // Livreur
  getLivreurs() : Observable<any> {
    return this.http.get(this.siteLink + "api/livreur")
  }

  postLivreur(livreur : Livreur) : Observable<any> {
    console.log("Livreur to post: ", livreur);
    return this.http.post<Livreur>(this.siteLink + "api/livreur", livreur)
  }

  putLivreur(livreur : Livreur) : Observable<any> {
    return this.http.put<Livreur>(`${this.siteLink}api/livreur/${livreur.idEmploye}`, livreur)
  }

  // async postLivreur(livreur: Livreur): Promise<any> {
  //   const response = await fetch('http://localhost:8080/api/livreur', {
  //     method: 'POST',
  //     headers: {
  //       'Content-Type': 'application/json'
  //     },
  //     body: JSON.stringify(livreur)
  //   }
  //   );
  //   return response.json();
  // }



  // EquipeLivreur
  

  getEquipeLivreur(): Observable<EquipeLivreursDTO[]> {
    return this.http.get<EquipeLivreursDTO[]>(this.siteLink + "api/equipes");
  }

  equipesLivreursFromDTO(dto: EquipeLivreursDTO[], allLivreurs: Livreur[]): EquipeLivreurs[] {
    if (!Array.isArray(dto) || !Array.isArray(allLivreurs)) {
      console.error('Invalid input:', { dto, allLivreurs });
      return [];
    }
  
    return dto.map(equipeDTO => {
      const livreurs = equipeDTO.livreursIds
        .map(id => allLivreurs.find(l => l.idEmploye === id))
        .filter((l): l is Livreur => l !== undefined);
  
      const equipe: EquipeLivreurs = {
        numEquipe: equipeDTO.numEquipe,
        status: equipeDTO.status as StatusEquipeLivreurs,
        livreurs: livreurs
      };
      return equipe;
    });
  }

  // equipeLivreursFromDTO(dto: EquipeLivreursDTO, allLivreurs: Livreur[]): EquipeLivreurs {
  //   const equipeLivreurs: EquipeLivreurs = {
  //     numEquipe: dto.numEquipe,
  //     status: dto.status as StatusEquipeLivreurs,
  //     livreurs: dto.livreursIds.map(id => allLivreurs.find(l => l.idEmploye === id)!),
  //     agenda: dto.agenda
  //   };
  //   return equipeLivreurs;
  // }

  // equipesLivreursFromDTO(dto: EquipeLivreursDTO[], allLivreurs: Livreur[]): EquipeLivreurs[] {
  //   return dto.map(e => this.equipeLivreursFromDTO(e, allLivreurs));
  // }


  postEquipeLivreur(dto: CreateEquipeLivreursDTO): Observable<any> {
    return this.http.post<any>(this.siteLink + "api/equipes", dto)
      .pipe(catchError(this.handleError));
  }

  postEquipeLivreurN(s:any): Observable<any> {
    return this.http.post<any>(this.siteLink + "api/equipes", s)
      .pipe(catchError(this.handleError));
  }
  

  // postEquipeLivreur(equipeLivreur : EquipeLivreurs) : Observable<any> {
  //   return this.http.post<EquipeLivreurs>("http://localhost:8080/api/equipes", equipeLivreur).pipe(catchError(this.handleError))
  // }

  updateEquipeLivreur(numEquipe: number, livreurs: Livreur[]): Observable<any> {
    const dto = {
      numEquipe: numEquipe,
      status: StatusEquipeLivreurs.PRET,
      livreurs: livreurs,
    };
    return this.http.put<EquipeLivreursDTO>(
      `${this.siteLink}api/equipes/${numEquipe}`, 
      dto
    ).pipe(catchError(this.handleError));
  }
  
  deleteEquipeLivreur(id: number): Observable<any> {
    return this.http.delete(`${this.siteLink}api/equipes/${id}`)
      .pipe(catchError(this.handleError));
  }

  // Commande

  getCommandes() : Observable<any> {
    return this.http.get(this.siteLink + "api/commandes").pipe(catchError(this.handleError))
  }

  putCommande(commande : Commande) : Observable<any> {
    return this.http.put<Commande>(`${this.siteLink}api/commandes/${commande.reference}`, commande).pipe(catchError(this.handleError))
  }

  // Tournee

  getTournees() : Observable<any> {
    return this.http.get<TourneeDTO>(this.siteLink + "api/tournee").pipe(catchError(this.handleError))
  }

  // tourneefromDTO(dto: TourneeDTO, allCommandes: Commande[],equipeLivreur: EquipeLivreurs[]): Tournee {
  //   const foundEquipe: EquipeLivreurs = equipeLivreur.find(e => e.numEquipe === dto.equipeLivreurId)!;
  //   const tournee: Tournee = {
  //     numTournee: dto.numTournee,
  //     date: dto.date,
  //     etat: dto.etat,
  //     equipeLivreur: { ...foundEquipe, status: foundEquipe.status as StatusEquipeLivreurs },
  //     commandes: dto.commandesIds.map(id => allCommandes.find(c => c.reference === id)!),
  //     distanceAParcourir: dto.distanceAParcourir,
  //     montant: dto.montant,
  //     tempsDeMontageTheorique: dto.tempsDeMontageTheorique,
  //     tempsDeMontageEffectif: dto.tempsDeMontageEffectif,
  //     camion: dto.camion,
  //     planificateur: dto.planificateur
  //   };
  //   return tournee;
  // }

tourneefromDTO(dto: TourneeDTO, allCommandes: Commande[], equipes: EquipeLivreurs[]): Tournee {
  const foundEquipe: EquipeLivreurs = equipes.find(e => e.numEquipe === dto.equipeLivreurId)!;
  const tournee: Tournee = {
      numTournee: dto.numTournee,
      date: dto.date,
      etat: dto.etat,
      equipeLivreur: { ...foundEquipe, status: foundEquipe.status as StatusEquipeLivreurs },
      // use the `commandes` array from the backend instead of mapping from commandesIds
      commandes: dto.commandes ? dto.commandes : [],
      distanceAParcourir: dto.distanceAParcourir,
      montant: dto.montant,
      tempsDeMontageTheorique: dto.tempsDeMontageTheorique,
      tempsDeMontageEffectif: dto.tempsDeMontageEffectif,
      camion: dto.camion,
      planificateur: dto.planificateur
  };
  return tournee;
}
  tourneesfromDTO(dto: TourneeDTO[], allCommandes: Commande[],equipeLivreur: EquipeLivreurs[]): Tournee[] {
    return dto.map(t => this.tourneefromDTO(t, allCommandes,equipeLivreur));
  }

  postTournee(tournee : any) : Observable<any> {
    return this.http.post<any>(this.siteLink + "api/tournee", tournee).pipe(catchError(this.handleError))
  }

  postTourneeN(tourneeNDTO : any) : Observable<any> {
    return this.http.post<any>(this.siteLink + "api/tournee", tourneeNDTO).pipe(catchError(this.handleError))
  }

  deleteTournee(numTournee: number): Observable<any> {
    return this.http.delete(`${this.siteLink}api/tournee/${numTournee}`)
      .pipe(catchError(this.handleError));
}


  private handleError(error: HttpErrorResponse) {
    let errorMsg = "⚠️ Impossible de récupérer les données. Vérifiez l’URL et réessayez.";
    if (error.error && error.error.message) {
      errorMsg = error.error.message;
    }
    console.error(`❌ Erreur HTTP ${error.status}:`, error.error);
    return throwError(() => new Error(errorMsg));
  }

}