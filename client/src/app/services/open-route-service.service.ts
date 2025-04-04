import { Injectable } from '@angular/core';
import { GeoJSON,LatLng } from 'leaflet';
import { EquipeLivreurs } from '../../models/interfaces/equipe-livreurs.model';
import { Commande } from '../../models/interfaces/commande.model';

@Injectable({
  providedIn: 'root'
})
export class OpenRouteServiceService {

  private readonly apiKey = '5b3ce3597851110001cf6248bcf05f5c9e6943378190c10a202f8b08';

  constructor() { }

  async getItinerary(latlngTable:LatLng[]):Promise<any>{
    const coordinates: number[][] = latlngTable.map(point => [point.lng, point.lat]);

    try{
      const fetchResponse = await fetch(
        "https://api.openrouteservice.org/v2/directions/driving-car/geojson",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: this.apiKey
          },
          body: JSON.stringify({
            coordinates: coordinates,
        })
        }
      )
      if (!fetchResponse.ok) {
        console.log('erreur en fetch ',fetchResponse.statusText);
        return;
      }
        const data = await fetchResponse.json();
        console.log('data haha',data);
        return data;
    }
    catch (error){
      console.log('erreur en fetch ',error);
      return;
    }
    
  }


  async getOptimization(latlngTable: LatLng[]): Promise<any> {

    const depot = latlngTable[0];

    const vehicles = [{
      id: 1,
      profile: 'driving-car',
      start: [depot.lng, depot.lat],
      end: [depot.lng, depot.lat],
      capacity: [latlngTable.length - 1]
    }];

    const jobs = latlngTable.slice(1).map((point, index) => ({
      id: index + 1,
      location: [point.lng, point.lat],
      amount: [1]
    }));

    const body = {
      vehicles,
      jobs,
      geometry: true
    };

    try {
      const fetchResponse = await fetch(
        "https://api.openrouteservice.org/optimization",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: this.apiKey
          },
          body: JSON.stringify(body)
        }
      );
      if (!fetchResponse.ok) {
        console.error('Erreur', fetchResponse.statusText);
        return;
      }
      const data = await fetchResponse.json();
      return data;
    }
    catch (error) {
      console.error('Erreur', error);
      return;
    }
  }


  async getOptimizationAutmatique(commandes: Commande[], availableEquipes: EquipeLivreurs[], entrepot: LatLng): Promise<any> {

    const baseCapacity = Math.floor(commandes.length / availableEquipes.length);
    const remainder = commandes.length % availableEquipes.length;
    interface Vehicle {
      id: number;
      profile: string;
      start: [number, number];
      end: [number, number];
      capacity: number[];
    }

    let vehicles: Vehicle[] = [];

    availableEquipes.forEach((equipe, index) => {
      vehicles.push({
        id: equipe.numEquipe,
        profile: 'driving-car',
        start: [entrepot.lng, entrepot.lat],
        end: [entrepot.lng, entrepot.lat],
        capacity: [index < remainder ? baseCapacity + 1 : baseCapacity]
      });
    });
    console.log('vehicles', vehicles);

    const jobs = commandes.map((commande, index) => ({
      id: commande.reference,
      location: [commande.client.adresse.longitude, commande.client.adresse.latitude],
      delivery: [1]
    }));

    const body = {
      vehicles,
      jobs,
      geometry: true
    };

    console.log('body', body);

    try {
      const fetchResponse = await fetch(
        "https://api.openrouteservice.org/optimization",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: this.apiKey
          },
          body: JSON.stringify(body)
        }
      );
      if (!fetchResponse.ok) {
        console.error('Erreur', fetchResponse.statusText);
        return;
      }
      const data = await fetchResponse.json();
      console.log('data', data);
      return data;
    }
    catch (error) {
      console.error('Erreur', error);
      return;
    }
  }
  
}