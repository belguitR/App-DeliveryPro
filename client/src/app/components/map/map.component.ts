import {
  Component, computed, effect, inject, input, model,
  OnInit, OnChanges,
  signal
} from '@angular/core';
import { LeafletModule } from '@bluehalo/ngx-leaflet';
import {
  LatLng, latLng, Layer, tileLayer
} from 'leaflet';
import { OpenRouteServiceService } from '../../services/open-route-service.service';
import { LeafletService } from '../../services/leaflet.service';
import { Commande } from '../../../models/interfaces/commande.model';
import { EquipeLivreurs } from '../../../models/interfaces/equipe-livreurs.model';
import * as L from 'leaflet';
import 'leaflet.markercluster';

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [LeafletModule],
  templateUrl: './map.component.html',
  styleUrl: './map.component.scss'
})
export class MapComponent implements OnInit, OnChanges {
  leafletService = inject(LeafletService);
  openRouteService = inject(OpenRouteServiceService);

  latitude = model<number>(45.1485200);
  longitude = model<number>(5.7369725);
  zoom = model<number>(14); // Zoom level increased for better visibility

  center = computed<LatLng>(() => latLng(this.latitude(), this.longitude()));
  options = computed(() => ({
    layers: [
      tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        attribution: '...'
      })
    ],
    zoom: this.zoom(),
    center: this.center()
  }));

  layers = signal<Layer[]>([
    this.leafletService.latLngToMarker(
      latLng(this.latitude(), this.longitude()),
      'entrepot'
    )
  ]);

  commandes = input.required<Commande[]>();
  polylineColors = input.required<string[]>();
  selectedEquipes = input.required<EquipeLivreurs[]>();
  createdTournees = model<LatLng[][]>([]);

  commandesMarkers: Layer[] = [];

  constructor() {
    effect(() => {
      if (this.commandesMarkers.length === 0 && this.commandes().length > 0) {
        this.initializeMarkers(this.commandes());
      }
    });
  }

  private async initializeMarkers(commandes: Commande[]) {
    if (!commandes || commandes.length === 0) return;

    let newLayers: Layer[] = [];
    for (let cmd of commandes) {
      newLayers.push(
        this.leafletService.latLngToMarker(
          latLng(cmd.client.adresse.latitude!, cmd.client.adresse.longitude!),
          'destination'
        )
      );
    }
    this.commandesMarkers = newLayers;

    const entrepotMarker = this.leafletService.latLngToMarker(
      latLng(this.latitude(), this.longitude()),
      'entrepot'
    );

    this.layers.set([entrepotMarker, ...this.commandesMarkers]);
  }

  async ngOnInit() {}

  clearTournees(): void {
    const usedLatLngs = this.createdTournees().flat();
    const remainingMarkers = this.commandesMarkers.filter(marker => {
      const markerLatLng = (marker as L.Marker).getLatLng();
      return !usedLatLngs.some(used =>
        used.lat === markerLatLng.lat && used.lng === markerLatLng.lng
      );
    });

    this.commandesMarkers = remainingMarkers;

    const entrepotMarker = this.leafletService.latLngToMarker(
      latLng(this.latitude(), this.longitude()),
      'entrepot'
    );

    this.layers.set([entrepotMarker, ...remainingMarkers]);
  }

  async ngOnChanges() {
    const tournees = this.createdTournees();
    const colors = this.polylineColors?.();

    if (tournees && tournees.length > 0) {
      for (let i = 0; i < tournees.length; i++) {
        const tournee = tournees[i];
        const color = colors?.[i] || this.leafletService.getRandomColor();

        try {
          const routePolyline = await this.leafletService.createPolylineForTournee(tournee, color);
          if (routePolyline) {
            this.layers.set([...this.layers(), routePolyline]);
          }
        } catch (error) {
          console.log('Error creating polyline for tournee', error);
        }
      }
    }
  }
}
