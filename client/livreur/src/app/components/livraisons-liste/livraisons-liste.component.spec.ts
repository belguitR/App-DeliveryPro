import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LivraisonsListeComponent } from './livraisons-liste.component';
import { LivraisonService } from '../../services/livraison.service';
import { signal } from '@angular/core';
import { By } from '@angular/platform-browser';

const dummyCommandes = [
    { client: { nom: 'A', prenom: 'AA', adresse: { adresse: 'Rue 1', ville: 'G' } } },
    { client: { nom: 'B', prenom: 'BB', adresse: { adresse: 'Rue 2', ville: 'L' } } },
];

class LivraisonStub {
    commandes             = signal<any[]>(dummyCommandes);
    originalIndex         = signal<number>(1);   // 2ᵉ livraison active
    allCommandesConfirmed = signal<boolean>(false);
}

describe('LivraisonsListeComponent', () => {
    let fixture: ComponentFixture<LivraisonsListeComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [LivraisonsListeComponent],
            providers: [{ provide: LivraisonService, useClass: LivraisonStub }]
        }).compileComponents();

        fixture = TestBed.createComponent(LivraisonsListeComponent);
        fixture.detectChanges();
    });

    it('devrait se créer', () => {
        expect(fixture.componentInstance).toBeTruthy();
    });

    it('affiche une <li> par commande', () => {
        const items = fixture.debugElement.queryAll(By.css('ul li'));
        expect(items.length).toBe(dummyCommandes.length);
    });

    it('ajoute la classe “active” sur la commande indexée originalIndex()', () => {
        const active = fixture.debugElement.queryAll(By.css('ul li'))[1];
        expect(active.nativeElement.classList).toContain('active');
    });
});
