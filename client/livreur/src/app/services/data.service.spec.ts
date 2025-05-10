import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { DataService } from './data.service';
import { EtatTournee } from '../../models/enums/etat-tournee.enum';

describe('DataService', () => {
    let service: DataService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
        });
        service   = TestBed.inject(DataService);
        httpMock  = TestBed.inject(HttpTestingController);
    });

    afterEach(() => httpMock.verify());

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('updateTourneeEtat émet un PATCH sur l’endpoint prévu', () => {
        service.updateTourneeEtat('REF123', EtatTournee.EN_COURS).subscribe();
        const req = httpMock.expectOne('http://localhost:8080/api/tournees/REF123/etat');
        expect(req.request.method).toBe('PATCH');
        expect(req.request.body).toEqual({ etat: EtatTournee.EN_COURS });
        req.flush({});
    });
});
