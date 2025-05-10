import { TestBed } from '@angular/core/testing';
import { LeafletService } from './leaflet.service';

describe('LeafletService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));
  it('should create', () => {
    const service = TestBed.inject(LeafletService);
    expect(service).toBeTruthy();
  });
});
