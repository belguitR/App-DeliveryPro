import { Component, computed, inject, model, signal } from '@angular/core';
import { LeafletModule } from '@bluehalo/ngx-leaflet';
import {
  LatLng,
  latLng,
  Layer,
  tileLayer
} from 'leaflet';

import { CartoService } from '../../services/carto.service';

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [LeafletModule],
  templateUrl: './map.component.html',
  styleUrl: './map.component.scss',

})

export class MapComponent {
  private cartoService = inject(CartoService);

  latitude = model<number>(45.14852);
  longitude = model<number>(5.7369725);
  zoom = model<number>(14);

  center = computed(() => latLng(this.latitude(), this.longitude()));
  options = computed(() => ({
    layers: [
      tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        attribution: 'Map data © OpenStreetMap contributors'
      })
    ],
    zoom: this.zoom(),
    center: this.center()
  }));

  layers = signal<Layer[]>([]);


  private getEntrepotMarker(): Layer {
    return this.cartoService.latLngToMarker(
      latLng(this.latitude(), this.longitude()),
      'entrepot'
    );
  }

  showLayers(layers: Layer[], keepEntrepot = true) {
    if (keepEntrepot) {
      this.layers.set([this.getEntrepotMarker(), ...layers]);
    } else {
      this.layers.set([...layers]);
    }
  }

  clearLayers(keepEntrepot = true) {
    if (keepEntrepot) {
      this.layers.set([this.getEntrepotMarker()]);
    } else {
      this.layers.set([]);
    }
  }
}
