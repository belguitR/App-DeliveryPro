import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MapComponent } from './map.component';
import { LeafletModule } from '@bluehalo/ngx-leaflet';
import { LeafletService } from '../../services/leaflet.service';
import { OpenRouteServiceService } from '../../services/open-route-service.service';
import { LivraisonService } from '../../services/livraison.service';
import { CartoService } from '../../services/carto.service';
import { signal } from '@angular/core';


class LeafletStub {}
class OrsStub {}
class LivraisonStub {
    tournee    = signal<any>(null);
    commandes  = signal<any[]>([]);
}
class CartoStub {}

describe('MapComponent', () => {
    let fixture: ComponentFixture<MapComponent>;
    let comp: MapComponent;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [MapComponent, LeafletModule],
            providers: [
                { provide: LeafletService,          useClass: LeafletStub },
                { provide: OpenRouteServiceService, useClass: OrsStub },
                { provide: LivraisonService,        useClass: LivraisonStub },
                { provide: CartoService,            useClass: CartoStub },
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(MapComponent);
        comp    = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('devrait se créer', () => {
        expect(comp).toBeTruthy();
    });

    it('clearLayers(false) vide complètement layers()', () => {
        comp.layers.set([{} as any]);          // faux calque pour le test
        comp.clearLayers(false);
        expect(comp.layers().length).toBe(0);
    });
});
