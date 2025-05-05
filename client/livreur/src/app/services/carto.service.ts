import { inject, Injectable } from '@angular/core';
import { Icon,icon,LatLng, marker, Marker, polyline, Polyline } from 'leaflet';
import { Commande } from '../../models/interfaces/commande.model';
import { catchError, map, Observable, of, tap } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CartoService {

  private readonly apiKey = '5b3ce3597851110001cf6248bcf05f5c9e6943378190c10a202f8b08';

  private http = inject(HttpClient);



  latLngToMarker(
    latLng: LatLng,
    entrDest: 'entrepot' | 'destination',
    enCours?: boolean
  ): Marker {
    let iconUrl: string;
    let iconRetinaUrl: string;
  
    if (entrDest === 'entrepot') {
      iconUrl = 'assets/images/entrepot.png';
      iconRetinaUrl = 'assets/images/entrepot.png';
    } else {
      // active livraison in red, others in blue
      const colorName = enCours ? 'red' : 'blue';
      iconUrl = `https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-${colorName}.png`;
      iconRetinaUrl = `https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-${colorName}.png`;
    }
  
    return marker([latLng.lat, latLng.lng], {
      icon: icon({
        ...Icon.Default.prototype.options,
        iconUrl,
        iconRetinaUrl,
        shadowUrl: 'assets/marker-shadow.png'
      })
    });
  }


getLatLngsFromCommandes(commandes: Commande[]): LatLng[] {
  const latLngs: LatLng[] = [];
  for (const commande of commandes) {
    const result = new LatLng(commande.client.coords.lat, commande.client.coords.lng)
    
      latLngs.push(result);
    
  }
  return latLngs;
}


createMarkersForAvailableCommandes(commandes: Commande[]): Marker[] {
  const latLngs = this.getLatLngsFromCommandes(commandes);
  return latLngs.map(lat => this.latLngToMarker(lat, 'destination'));
} 

getItinerary(latlngTable: LatLng[]): Observable<any> {
  const coordinates = latlngTable.map(p => [p.lng, p.lat] as [number, number]);
  const body = { coordinates };
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    Authorization: this.apiKey
  });

  return this.http
    .post<any>(
      'https://api.openrouteservice.org/v2/directions/driving-car/geojson',
      body,
      { headers }
    )
    .pipe(
      tap(res => console.log('ORS itinerary:', res)),
      catchError(err => {
        console.error('ORS itinerary error:', err);
        return of(null);
      })
    );
}

createPolylineFromCommandes(
  commandes: Commande[],
  color: string = 'blue',
  entrepot?: LatLng,
  includeStart: boolean = false,
  includeEnd: boolean = false
): Observable<Polyline | null> {
  let latlngs = this.getLatLngsFromCommandes(commandes);

  if (entrepot) {
    if (includeStart) latlngs = [entrepot, ...latlngs];
    if (includeEnd) latlngs = [...latlngs, entrepot];
  }

  if (latlngs.length < 2) {
    return of(null);
  }
  return this.getItinerary(latlngs).pipe(
    map(data => {
      if (!data?.features?.length) {
        console.error('Invalid itinerary response:', data);
        return null;
      }
      const coords: [number, number][] = data.features[0].geometry.coordinates;
      const pts = coords.map(([lng, lat]) => new LatLng(lat, lng));
      return polyline(pts, { color });
    }),
    catchError(err => {
      console.error('Polyline creation error:', err);
      return of(null);
    })
  );
}

  
}