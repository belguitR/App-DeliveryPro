import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ServerApiService {

  getHelloWorld(): Promise<string> {
    return fetch('/api/hello-world/').then(response => response.text());
  }

}
