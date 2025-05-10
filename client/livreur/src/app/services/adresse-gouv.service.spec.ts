import { TestBed } from '@angular/core/testing';
import { AdresseGouvService } from './adresse-gouv.service';


describe('AdresseGouvService', () => {
  let service: AdresseGouvService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdresseGouvService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('geocode mappe la réponse GeoJSON en LatLng[]', async () => {
    const fakeFeature = { geometry: { coordinates: [5.72, 45.18] } };
    spyOn(globalThis as any, 'fetch').and.resolveTo(
      new Response(JSON.stringify({ features: [fakeFeature] }), {
        status: 200,
        headers: { 'Content-Type': 'application/json' }
      })
    );

    const result = await service.geocode('dummy address');
    expect(result).toHaveSize(1);
    expect(result[0].lat).toBeCloseTo(45.18, 3);
    expect(result[0].lng).toBeCloseTo(5.72, 3);
  });
});
