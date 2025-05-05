import { TestBed } from '@angular/core/testing';
import { PlanificationService } from './planification.service';
import { DataService } from './data.service';
import { CartoService } from './carto.service';
import { KMeansService } from './kmeans.service';
import { of } from 'rxjs';
import { LatLng } from 'leaflet';
import { EtatTournee } from '../../models/enums/etat-tournee.enum';


describe('TESTS PlanificationService', () => {
  let service: PlanificationService;
  let mockDataService: jasmine.SpyObj<DataService>;
  let mockCartoService: jasmine.SpyObj<CartoService>;
  let mockKMeansService: jasmine.SpyObj<KMeansService>;

  beforeEach(() => {
    mockDataService = jasmine.createSpyObj('DataService', [
      'journees', 'commandes', 'planificateurEmail', 'createJournee'
    ]);
    mockCartoService = jasmine.createSpyObj('CartoService', [
      'getOptimizationAutmatique', 'getColorForTeam'
    ]);
    mockKMeansService = jasmine.createSpyObj('KMeansService', [
      'kmeans'
    ]);

    TestBed.configureTestingModule({
      providers: [
        PlanificationService,
        { provide: DataService, useValue: mockDataService },
        { provide: CartoService, useValue: mockCartoService },
        { provide: KMeansService, useValue: mockKMeansService },
      ],
    });

    service = TestBed.inject(PlanificationService);
  });

  it('getTomorrowDate should return correct format', () => {
    const result = service.getTomorrowDate();
    expect(result).toMatch(/\d{4}-\d{2}-\d{2}/);
  });

  it('generateTourneeReference should generate correctly', () => {
    const result = service.generateTourneeReference([{ reference: 'TRN002' } as any]);
    expect(result).toBe('TRN003');
  });

  it('creerTournees should create tournees with shortest strategy', async () => {
    mockDataService.journees.and.returnValue([]);
    mockDataService.commandes.and.returnValue([]);
    mockDataService.planificateurEmail.and.returnValue('test@test.com');
    mockDataService.createJournee.and.returnValue(of({}));
    mockCartoService.getColorForTeam.and.returnValue('#ff0000');
    mockCartoService.getOptimizationAutmatique.and.returnValue(of({
      routes: [{
        summary: { distance: 100 },
        steps: [
          { type: 'job', job: 1, location: [1, 2] },
          { type: 'job', job: 2, location: [3, 4] },
        ]
      }]
    }));
    mockKMeansService.kmeans.and.returnValue({
      clusters: [0, 0],
      centroids: [],
      iterations: 1,
      converged: true
    });

    const commandes = [
      { reference: 'CMD1', client: { coords: { lat: 1, lng: 2 } } },
      { reference: 'CMD2', client: { coords: { lat: 3, lng: 4 } } }
    ];

    const equipes = [
      { idEquipe: 1, livreurIds: [] }
    ];

    const camions = [
      { code: 'CAM1' }
    ];

    const result = await service.creerTournees(
      equipes as any,
      commandes as any,
      new LatLng(0, 0),
      'shortest',
      '2025-05-05',
      camions as any
    );

    expect(result.routes.length).toBe(1);
    expect(mockDataService.createJournee).toHaveBeenCalled();
  });
});
