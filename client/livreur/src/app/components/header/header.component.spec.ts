// header.component.spec.ts
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HeaderComponent } from './header.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';

// 🪄  Stub minimal de AuthService
class AuthStub {
  logout   = jasmine.createSpy('logout');
  user     = jasmine.createSpy('user').and.returnValue(true);
  username = jasmine.createSpy('username').and.returnValue('Bob');
}

describe('HeaderComponent', () => {
  let fixture: ComponentFixture<HeaderComponent>;
  let router: Router;
  let auth: AuthStub;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderComponent, RouterTestingModule],
      providers: [{ provide: AuthService, useClass: AuthStub }]
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    router  = TestBed.inject(Router);
    auth    = TestBed.inject(AuthService) as unknown as AuthStub;

    spyOn(router, 'navigate');
    fixture.detectChanges();
  });

  it('devrait se créer', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('clic sur “Se déconnecter” → auth.logout puis navigate', () => {
    const btn = fixture.debugElement.query(By.css('.profile-dropdown-content button'));
    btn.triggerEventHandler('click');
    expect(auth.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
