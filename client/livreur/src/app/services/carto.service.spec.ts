import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CartoService } from './carto.service';
import { LatLng } from 'leaflet';

describe('CartoService', () => {
    let service: CartoService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
        });
        service = TestBed.inject(CartoService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('latLngToMarker renvoie un Marker positionné sur les coords', () => {
        const pos = new LatLng(45, 5);
        const m = service.latLngToMarker(pos, 'entrepot');
        expect(m.getLatLng()).toEqual(pos);
    });

    it('getLatLngsFromCommandes extrait bien les coords des commandes', () => {
        const commandes = [
            { client: { coords: { lat: 1, lng: 2 } } },
            { client: { coords: { lat: 3, lng: 4 } } }
        ] as any;
        expect(service.getLatLngsFromCommandes(commandes).map(c => [c.lat, c.lng]))
            .toEqual([[1, 2], [3, 4]]);
    });
});
