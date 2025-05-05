// dashboard.component.ts
import { Component, inject, signal,effect, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MapComponent } from '../components/map/map.component';
import { HeaderComponent } from '../components/header/header.component';
import { LivraisonEnCoursComponent } from '../components/livraison-en-cours/livraison-en-cours.component';
import { LivraisonsListeComponent } from '../components/livraisons-liste/livraisons-liste.component';
import { DataService } from '../services/data.service';
import { LivraisonService } from '../services/livraison.service';
import { CartoService } from '../services/carto.service';
import { latLng, LatLng, Layer } from 'leaflet';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    FormsModule,
    MapComponent,
    HeaderComponent,
    MatSlideToggleModule,
    LivraisonEnCoursComponent,
    LivraisonsListeComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {

  @ViewChild(MapComponent) mapRef!: MapComponent;

  private dataService     = inject(DataService);
  private livraisonService = inject(LivraisonService);
  private cartoService = inject(CartoService);

  entrepot = signal<LatLng>(latLng(45.14852, 5.7369725));

  readonly commandesLeft = this.livraisonService.commandesLeft;
  readonly tournee = this.dataService.tournee;
  readonly index = this.livraisonService.activeIndex;

  readonly activeCommande = this.livraisonService.activeCommande;

  constructor() {
    effect(() => {
      const list = this.commandesLeft();
      if (!this.mapRef || list.length === 0) return;
    
      const entrepotM = this.cartoService.latLngToMarker(
        this.entrepot(), 'entrepot', false
      );
      const cmdMs = list.map(cmd =>
        this.cartoService.latLngToMarker(
          latLng(cmd.client.coords.lat, cmd.client.coords.lng),
          'destination',
          cmd.reference === this.activeCommande()?.reference
        )
      );
    
      const allCommandes = this.livraisonService.commandes();
      const includeStart = list.length === allCommandes.length;
      const includeEnd = list.length === 1;
    
      this.cartoService
        .createPolylineFromCommandes(
          list,
          'blue',
          this.entrepot(),
          includeStart,
          true
        )
        .subscribe(poly => {
          this.mapRef.clearLayers(true);
    
          const layers: Layer[] = [entrepotM, ...cmdMs];
          if (poly) {
            layers.push(poly);
          }
          this.mapRef.showLayers(layers, true);
        });
    });

  }

 


}