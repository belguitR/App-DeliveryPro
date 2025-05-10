import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LivraisonEnCoursComponent } from './livraison-en-cours.component';
import { LivraisonService } from '../../services/livraison.service';
import { DataService } from '../../services/data.service';
import { signal } from '@angular/core';
import { By } from '@angular/platform-browser';


class LivraisonStub {
    // signaux attendus par le composant
    commandesLeft   = signal<any[]>([]);
    activeCommande  = signal<any>(null);
    activeIndex     = signal<number>(0);

    // méthodes espionnées
    startTournee      = jasmine.createSpy('startTournee');
    confirmLivraison  = jasmine.createSpy('confirmLivraison');
    signalerLivraison = jasmine.createSpy('signalerLivraison');
}

class DataStub {
    // le template appelle tournee() : il faut donc un signal (fonction)
    tournee = signal<any>({ etat: 'PLANIFIEE' });
}
/* -------------------------------- */

describe('LivraisonEnCoursComponent', () => {
    let fixture: ComponentFixture<LivraisonEnCoursComponent>;
    let livStub: LivraisonStub;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [LivraisonEnCoursComponent],
            providers: [
                { provide: LivraisonService, useClass: LivraisonStub },
                { provide: DataService,     useClass: DataStub }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(LivraisonEnCoursComponent);
        livStub = TestBed.inject(LivraisonService) as unknown as LivraisonStub;
        fixture.detectChanges();          // ⚠️ important pour jouer le template
    });

    it('devrait se créer', () => {
        expect(fixture.componentInstance).toBeTruthy();
    });

    it('bouton “Commencer la tournée” → startTournee()', () => {
        const btn = fixture.debugElement.query(By.css('.confirm-button'));
        btn.triggerEventHandler('click');
        expect(livStub.startTournee).toHaveBeenCalled();
    });

    it('onConfirm appelle confirmLivraison()', () => {
        fixture.componentInstance.onConfirm();
        expect(livStub.confirmLivraison).toHaveBeenCalled();
    });

    it('onSignal appelle signalerLivraison()', () => {
        fixture.componentInstance.onSignal();
        expect(livStub.signalerLivraison).toHaveBeenCalled();
    });
});
