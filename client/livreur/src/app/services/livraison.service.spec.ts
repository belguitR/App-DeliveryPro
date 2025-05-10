import { TestBed } from '@angular/core/testing';
import { LivraisonService } from './livraison.service';
import { DataService } from './data.service';
import { Signal, signal } from '@angular/core';
import { of } from 'rxjs';


class DataStub {
    tournee = signal<any>(null);
    commandes = signal<any[]>([]);
    updateTourneeEtat = jasmine.createSpy().and.returnValue(of({}));
    updateCommande     = jasmine.createSpy().and.returnValue(of({}));
    refreshCommandesForTournee = jasmine.createSpy();
}

describe('LivraisonService', () => {
    let service: LivraisonService;
    let dataStub: DataStub;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [{ provide: DataService, useClass: DataStub }],
        });
        service  = TestBed.inject(LivraisonService);
        dataStub = TestBed.inject(DataService) as unknown as DataStub;
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('startTournee ne fait rien si tournee non PLANIFIEE', () => {
        dataStub.tournee.set({ etat: 'AUTRE' });
        service.startTournee();
        expect(dataStub.updateTourneeEtat).not.toHaveBeenCalled();
    });
});
