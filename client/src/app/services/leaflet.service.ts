import { Injectable,inject } from '@angular/core';
import { GeoJSON } from 'leaflet';
import { latLng, LatLng, Marker,marker,icon,Icon,Layer,Polyline,polyline ,circleMarker,CircleMarker} from 'leaflet';
import { Commande } from '../../models/interfaces/commande.model';
import { AdresseGouvService } from './adresse-gouv.service';
import { OpenRouteServiceService } from './open-route-service.service';

@Injectable({
  providedIn: 'root'
})
export class LeafletService {

  constructor() { }

  adresseGouvService = inject(AdresseGouvService);
  openRouteService = inject(OpenRouteServiceService);
  private teamColorMap = new Map<number, string>();

  
  getColorForTeam(teamId: number): string {
    if (!this.teamColorMap.has(teamId)) {
      this.teamColorMap.set(teamId, this.getRandomColor());
    }
    return this.teamColorMap.get(teamId)!;
  }

  
  latLngToMarker(latLng: LatLng, entrDest: 'entrepot' | 'destination',color?: "red"|"blue"): Marker {
    let iconUrl = entrDest === 'entrepot'
      ? 'assets/images/entrepot.png'
      : 'assets/marker-icon.png';

    let iconRetinaUrl = entrDest === 'entrepot'
      ? 'assets/images/entrepot.png'
      : 'assets/marker-icon-2x.png';

      if (color === "red") {
        iconUrl = 'assets/images/red-marker.png';
        iconRetinaUrl = 'assets/images/red-marker.png';
      }

    return marker([latLng.lat, latLng.lng], {
      icon: icon({
        ...Icon.Default.prototype.options,
        iconUrl: iconUrl,
        iconRetinaUrl: iconRetinaUrl,
        shadowUrl: 'assets/marker-shadow.png'
      })
    });
  }

  // ( ai dzaer ) form mte3 lmarkr ig idfkk 
  circleMarkerForTeam(latlng: LatLng, color: string): CircleMarker {
    return circleMarker(latlng, {
      radius: 6,
      color: color,
      fillColor: color,
      fillOpacity: 1,
      weight: 1
    });
  }
  
  

  latLngToPolyline(data: LatLng[], color?: string): Polyline {
    const chosenColor = color || this.getRandomColor();
    const poly = polyline(data, {
      color: chosenColor,
      weight: 4,
      opacity: 0.8
    });
    console.log("polyline:", poly);
    return poly;
  }
  

  public  getRandomColor(): string {
    return '#' + Math.floor(Math.random() * 16777215).toString(16).padStart(6, '0');
  }

  private async getLatLngsFromCommandes(commandes: Commande[]): Promise<LatLng[]> {
    const latLngs: LatLng[] = [];
    for (const commande of commandes) {
      const result = latLng(commande.client.adresse.latitude!, commande.client.adresse.longitude!)
      
        latLngs.push(result);
      
    }
    return latLngs;
  }


  async createMarkersForAvailableCommandes(commandes: Commande[]): Promise<Marker[]> {
    const latLngs = await this.getLatLngsFromCommandes(commandes);
    return latLngs.map(lat => this.latLngToMarker(lat, 'destination'));
  } 


  async commandesAddressesToLatLngs(commandes: Commande[]): Promise<LatLng[]> {
    return await this.getLatLngsFromCommandes(commandes);
  }

  async createPolylineForTournee(tournee: LatLng[], color: string = '#3388ff'): Promise<Polyline | null> {
    if (tournee.length < 2) {
      return null;
    }
  
    try {
      const data = await this.openRouteService.getItinerary(tournee);
  
      if (!data || !data.features || data.features.length === 0) {
        console.error(' Invalid itinerary response:', data);
        return null;
      }
  
      const coordinates = data.features[0].geometry.coordinates;
      const itineraryLatLng = coordinates.map(
        ([lng, lat]: number[]) => new LatLng(lat, lng)
      );
  
      return polyline(itineraryLatLng, { color }); 
    } catch (error) {
      
      return null;
    }
  }
  
  


}