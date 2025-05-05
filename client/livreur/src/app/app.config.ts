import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { routes } from './app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { initializeApp, provideFirebaseApp } from '@angular/fire/app';
import { getFirestore, provideFirestore } from '@angular/fire/firestore';
import { getAuth } from 'firebase/auth';
import { provideAuth } from '@angular/fire/auth';

const firebaseConfig = {
  apiKey: "AIzaSyByaj-oDzvcBb5b8eSUM3CbczoJXvfUSuI",
  authDomain: "projet-livreur-26a60.firebaseapp.com",
  projectId: "projet-livreur-26a60",
  storageBucket: "projet-livreur-26a60.firebasestorage.app",
  messagingSenderId: "715030504973",
  appId: "1:715030504973:web:aa4f2a907eb7f52e645744",
  measurementId: "G-6RFPKWLHQK"
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes), provideAnimationsAsync(),
    provideHttpClient(),
    provideFirebaseApp(() => initializeApp(firebaseConfig)),
    provideAuth(() => getAuth()),
    provideFirestore(() => getFirestore()),
  ]
};
