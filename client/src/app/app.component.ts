import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ServerApiService } from './services/server-api.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  protected readonly response = signal<string>("");

  /**
   * Binding to the service
   */
  private readonly _srvApi = inject(ServerApiService);

  getHelloWorld(): void {
    this._srvApi.getHelloWorld().then(
      str => this.response.set(str)
    );
  }
}
