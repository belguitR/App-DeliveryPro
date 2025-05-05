import { TestBed, ComponentFixture } from '@angular/core/testing';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { TourneesComponent } from './tournees.component';
import { DataService } from '../../services/data.service';
import { EquipeLivreurs } from '../../../models/interfaces/equipe-livreurs.model';
import { Livreur } from '../../../models/interfaces/livreur.model';
import { Commande } from '../../../models/interfaces/commande.model';


class DataServiceStub {
  journees = signal<any[]>([{ reference: 'J1' }]);

  equipesLivreurs = signal<EquipeLivreurs[]>([
    { idEquipe: 1, status: 'PRET' } as unknown as EquipeLivreurs,
  ]);

  livreurs = signal<Livreur[]>([
    { trigramme: 'ABC', nom: 'Alice', prenom: 'B.', tel: '' } as unknown as Livreur,
    { trigramme: 'DEF', nom: 'Daniel', prenom: 'E.', tel: '' } as unknown as Livreur,
  ]);

  commandes = signal<Commande[]>([
    { reference: 'C1', etat: 0 } as unknown as Commande,
    { reference: 'C2', etat: 0 } as unknown as Commande,
  ]);

  // HTTP‑like calls mocked with observables
  deleteJournee = jasmine.createSpy('deleteJournee').and.returnValue(of(null));
  refreshJournees = jasmine.createSpy('refreshJournees');
  refreshCommandes = jasmine.createSpy('refreshCommandes');
  refreshEquipes = jasmine.createSpy('refreshEquipes');
  refreshLivreurs = jasmine.createSpy('refreshLivreurs');
}

/**************************
 *       TEST SUITE       *
 **************************/

describe('TESTS TourneesComponent', () => {
  let fixture: ComponentFixture<TourneesComponent>;
  let component: TourneesComponent;
  let data: DataServiceStub;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TourneesComponent],
      providers: [{ provide: DataService, useClass: DataServiceStub }],
      schemas: [],
    })
      // override the template to focus on class logic and avoid template-dependent errors
      .overrideComponent(TourneesComponent, {
        set: { template: '' },
      })
      .compileComponents();

    fixture = TestBed.createComponent(TourneesComponent);
    component = fixture.componentInstance;
    data = TestBed.inject(DataService) as unknown as DataServiceStub;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('getEquipeById should find equipe', () => {
    const eq = component.getEquipeById('1');
    expect(eq?.idEquipe).toBe(1);
  });

  it('getLivreursFromIds should map ids to livreurs', () => {
    const livs = component.getLivreursFromIds(['ABC', 'DEF']);
    expect(livs.length).toBe(2);
    expect(livs[0].trigramme).toBe('ABC');
  });

  it('getCommandeByReference should return commande', () => {
    const cmd = component.getCommandeByReference('C2');
    expect(cmd?.reference).toBe('C2');
  });

  describe('#deleteJournee', () => {
    it('should abort if user cancels', () => {
      spyOn(window, 'confirm').and.returnValue(false);
      component.deleteJournee('J1');
      expect(data.deleteJournee).not.toHaveBeenCalled();
    });

    it('should call deleteJournee and refresh methods if confirmed', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      component.deleteJournee('J1');
      expect(data.deleteJournee).toHaveBeenCalledWith('J1');
      expect(data.refreshJournees).toHaveBeenCalled();
      expect(data.refreshCommandes).toHaveBeenCalled();
      expect(data.refreshEquipes).toHaveBeenCalled();
      expect(data.refreshLivreurs).toHaveBeenCalled();
    });
  });
});
