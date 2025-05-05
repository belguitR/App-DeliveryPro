import { inject, Injectable, signal } from '@angular/core';
import { Auth, signInWithEmailAndPassword, signOut, User } from '@angular/fire/auth';
import { from, Observable, of, switchMap, tap } from 'rxjs';
import { Firestore, doc, getDoc } from '@angular/fire/firestore';
import { DataService } from './data.service';

@Injectable({
    providedIn: 'root',
})
export class AuthService {
    private auth = inject(Auth)
    private firestore = inject(Firestore)
    private dataService = inject(DataService);


    user = signal<User | null>(null);
    username = signal<string | null>(null);

    constructor() {
        this.auth.onAuthStateChanged(async (user) => {
            this.user.set(user);
            this.dataService.setUserEmail(user?.email ?? null);

            if (user) {
              await this.fetchUsername(user.uid);
            } else {
              this.username.set(null);
            }
          });
    }

    login(email: string, password: string): Observable<void> {
      return from(signInWithEmailAndPassword(this.auth, email, password)).pipe(
        tap(cred => {
          this.dataService.setUserEmail(cred.user?.email ?? null);
        }),
        switchMap(userCredential => {
          if (userCredential.user) {
            return from(this.fetchUsername(userCredential.user.uid)).pipe(
              switchMap(() => of(undefined))
            );
          }
          return of(undefined);
        })
      );
    }

    logout() : Observable<void> {
        try {
            const user = signOut(this.auth);
            this.dataService.setUserEmail(null);
            return from(user)
        } catch (error) {
            throw error;
        }
    }

    private async fetchUsername(uid: string) {
        const userDoc = await getDoc(doc(this.firestore, `users/${uid}`));
        if (userDoc.exists()) {
          this.username.set(userDoc.data()['username']);
        } else {
          this.username.set(null);
        }
      }
}