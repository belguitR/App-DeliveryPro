import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { PlanificateurAutomatiqueComponent } from './planificateur-automatique.component';
import { MapComponent } from '../map/map.component';
import { LeafletModule } from '@bluehalo/ngx-leaflet';
import * as L from 'leaflet';
import { signal } from '@angular/core';
import { PlanificationService } from '../../services/planification.service';
import { DataService } from '../../services/data.service';
import { CartoService } from '../../services/carto.service';
import { latLng, LatLng } from 'leaflet';
import { EquipeLivreurs } from '../../../models/interfaces/equipe-livreurs.model';
import { Commande } from '../../../models/interfaces/commande.model';


class CartoServiceStub {
  latLngToMarker = jasmine
    .createSpy('latLngToMarker')
    .and.callFake((p: LatLng, _type: string, _teamId?: number) => L.marker(p));

  createPolylineForTournee = jasmine
    .createSpy('createPolylineForTournee')
    .and.callFake((tour: LatLng[], _teamId: number) => ({
      toPromise: () => Promise.resolve(L.polyline(tour)),
    }));

  getDistanceFromORS = jasmine
    .createSpy('getDistanceFromORS')
    .and.callFake(() => Promise.resolve(10));
}

class DataServiceStub {
  equipesLivreurs = signal<EquipeLivreurs[]>([
    { idEquipe: 1, status: 'PRET' } as unknown as EquipeLivreurs,
    { idEquipe: 2, status: 'BUSY' } as unknown as EquipeLivreurs,
  ]);

  commandesOuvertes = signal<Commande[]>([
    {
      reference: 'C1',
      client: { coords: { lat: 0, lng: 0 } },
    } as unknown as Commande,
    {
      reference: 'C2',
      client: { coords: { lat: 1, lng: 1 } },
    } as unknown as Commande,
  ]);

  camions = signal<any[]>([
    {
      code: 'TR1',
      typeCamion: { image: 'img', nom: 'Truck' },
      kilometrage: 50,
      immatriculation: 'AA-111-AA',
    },
  ]);
}

class PlanificationServiceStub {
  getTomorrowDate = jasmine
    .createSpy('getTomorrowDate')
    .and.returnValue(new Date(Date.now() + 86_400_000).toISOString().slice(0, 10));

  loadTournees = jasmine.createSpy('loadTournees').and.callFake(() =>
    Promise.resolve({
      routes: [[latLng(0, 0), latLng(0, 1), latLng(0, 0)]],
      teamIdToColor: new Map<number, string>([[1, '#ff0000']]),
      equipes: [{ idEquipe: 1, status: 'PRET' } as unknown as EquipeLivreurs],
      loaded: [
        {
          reference: 'C1',
          client: { coords: { lat: 0, lng: 0 } },
        } as unknown as Commande,
      ],
    })
  );

  creerTournees = jasmine.createSpy('creerTournees').and.callFake(() =>
    Promise.resolve({
      routes: [[latLng(0, 0), latLng(1, 1), latLng(0, 0)]],
      teamIdToColor: new Map<number, string>([[1, '#ff0000']]),
    })
  );

  checkIfJourneeExists = jasmine
    .createSpy('checkIfJourneeExists')
    .and.callFake(() => Promise.resolve(false));

  getEquipeColor = jasmine
    .createSpy('getEquipeColor')
    .and.callFake(() => '#ff0000');
}

/**
 * ######################
 * #     TEST     #
 * ######################
 */

describe('TESTS PlanificateurAutomatiqueComponent', () => {
  let fixture: ComponentFixture<PlanificateurAutomatiqueComponent>;
  let component: PlanificateurAutomatiqueComponent;
  let carto: CartoServiceStub;
  let planif: PlanificationServiceStub;
  let showLayersSpy: jasmine.Spy;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        LeafletModule,
        PlanificateurAutomatiqueComponent, // standalone component under test
      ],
      providers: [
        { provide: CartoService, useClass: CartoServiceStub },
        { provide: DataService, useClass: DataServiceStub },
        { provide: PlanificationService, useClass: PlanificationServiceStub },
      ],
    }).compileComponents();

    // Spy must be created inside before/beforeEach
    showLayersSpy = spyOn(MapComponent.prototype as any, 'showLayers').and.callThrough();

    fixture = TestBed.createComponent(PlanificateurAutomatiqueComponent);
    component = fixture.componentInstance;
    carto = TestBed.inject(CartoService) as unknown as CartoServiceStub;
    planif = TestBed.inject(
      PlanificationService
    ) as unknown as PlanificationServiceStub;

    fixture.detectChanges(); // triggers ngAfterViewInit
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should filter availableEquipeLivreurs to only PRET teams', () => {
    expect(component.availableEquipeLivreurs().length).toBe(1);
    expect(component.availableEquipeLivreurs()[0].status).toBe('PRET');
  });

  it('should call CartoService.latLngToMarker for each commande on init', () => {
    // Called once for entrepôt + two commandes
    expect(carto.latLngToMarker).toHaveBeenCalledTimes(4); // 1 entrepôt + 2 commandes + 1 entrepôt ajouté par MapComponent
  });

  it('should call MapComponent.showLayers during ngAfterViewInit', () => {
    expect(showLayersSpy).toHaveBeenCalled();
  });

  it('loadTourneesForSelectedDate should set isLoadMode to true and populate routes', fakeAsync(async () => {
    await component.loadTourneesForSelectedDate();
    tick();

    expect(planif.loadTournees).toHaveBeenCalled();
    expect(component.isLoadMode()).toBeTrue();
    expect(component.createdTourneesSignal().length).toBe(1);
  }));
});
