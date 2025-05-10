import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { map, catchError, switchMap } from 'rxjs/operators';
import { Commande, commandeSchema } from '../../models/interfaces/commande.model';
import { Tournee, tourneeSchema } from '../../models/interfaces/tournee.model';
import { EtatCommande } from '../../models/enums/etat-commande.enum';
import { toSignal } from '@angular/core/rxjs-interop';
import { EtatTournee } from '../../models/enums/etat-tournee.enum';

@Injectable({ providedIn: 'root' })
export class DataService {
  private http = inject(HttpClient);

  private readonly apiBaseUrl = 'http://localhost:8080';
  //private readonly apiBaseUrl = '';

  userEmail = signal<string | null>(null);

  setUserEmail(email: string | null) {
    this.userEmail.set(email);
    this.refreshTournee();
  }

  // ─── Tournee Stream for livreur ─────────────────────────────────
  private tourneeRefresh$ = new BehaviorSubject<void>(undefined);

  readonly tournee = toSignal(
    this.tourneeRefresh$.pipe(
      switchMap(() => {
        const mail = this.userEmail();
        if (!mail) return of(null as Tournee | null);
        return this.http
          .get<unknown>(`${this.apiBaseUrl}/api/tournees/last/by-livreur/${encodeURIComponent(mail)}`)
          .pipe(
            map(raw => {
              try {
                return tourneeSchema.parse(raw);
              } catch (err) {
                console.error('Zod validation failed for tournee:', err);
                return null;
              }
            }),
            catchError(err => {
              console.error('Error fetching tournee:', err);
              return of(null);
            })
          );
      })
    ),
    { initialValue: null as Tournee | null }
  );

  refreshTournee() {
    this.tourneeRefresh$.next();
  }

  updateTourneeEtat(reference: string, etat: EtatTournee) {
    return this.http.patch(`${this.apiBaseUrl}/api/tournees/${encodeURIComponent(reference)}/etat`, { etat });
  }

  // ─── Commandes Stream for current tournee ──────────────────────
  private commandesRefresh$ = new BehaviorSubject<string | null>(null);

  readonly commandes = toSignal(
    this.commandesRefresh$.pipe(
      switchMap(tRef => {
        if (!tRef) return of([] as Commande[]);
        return this.http
          .get<unknown>(`${this.apiBaseUrl}/api/commandes/tournee/${encodeURIComponent(tRef)}`)
          .pipe(
            map(raw => {
              try {
                return commandeSchema.array().parse(raw);
              } catch (e) {
                console.error('Zod failed on commandes[] for tournee', tRef, e);
                return [] as Commande[];
              }
            }),
            catchError(err => {
              console.error('HTTP GET /api/commandes/{ref} failed:', err);
              return of([] as Commande[]);
            })
          );
      })
    ),
    { initialValue: [] as Commande[] }
  );

  refreshCommandesForTournee(tourneeReference: string | null) {
    this.commandesRefresh$.next(tourneeReference);
  }

  updateCommande(
    reference: string,
    payload: { etat: EtatCommande; dateDeLivraison: string }
  ): Observable<Commande> {
    return this.http
      .patch<unknown>(`${this.apiBaseUrl}/api/commandes/${reference}`, payload)
      .pipe(
        map(raw => commandeSchema.parse(raw)),
        catchError(this.handleError)
      );
  }

  private handleError(err: HttpErrorResponse) {
    console.error('DataService error', err);
    return throwError(() => err);
  }
}