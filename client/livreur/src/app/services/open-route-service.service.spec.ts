import { TestBed } from '@angular/core/testing';
import { OpenRouteServiceService } from './open-route-service.service';
import { LatLng } from 'leaflet';

describe('OpenRouteServiceService', () => {
  let service: OpenRouteServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OpenRouteServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getItinerary POSTe bien le tableau de coords', async () => {
    const fetchSpy = spyOn(globalThis as any, 'fetch')
      .and.callFake(async (input: RequestInfo | URL, init?: RequestInit) => {
        const sent = JSON.parse(init!.body as string);
        expect(sent.coordinates.length).toBe(2);
        return new Response(JSON.stringify({ routes: [] }), { status: 200 });
      });

    const coords = [new LatLng(1, 2), new LatLng(3, 4)];
    await service.getItinerary(coords);
    expect(fetchSpy).toHaveBeenCalled();
  });
});
