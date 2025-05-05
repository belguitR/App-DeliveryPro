import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HeaderComponent } from './header.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { signal } from '@angular/core'; // Import signal
import { User } from '@angular/fire/auth'; // Import User type if needed

// Updated MockAuthService
class MockAuthService {
  // Use signals for user and username as in the actual service
  user = signal<User | null>(null);
  username = signal<string | null>(null);
  // Mock logout to return an Observable
  logout = jasmine.createSpy('logout').and.returnValue(of(undefined));

  // Helper to set user/username for tests
  setCurrentUser(email: string | null, name: string | null) {
    this.user.set(email ? { email } as User : null); // Simulate user object
    this.username.set(name);
  }
}

// MockRouter remains the same
class MockRouter {
  navigate = jasmine.createSpy('navigate');
}

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let authService: MockAuthService;
  let router: MockRouter;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderComponent], // Import the standalone component
      providers: [
        { provide: AuthService, useClass: MockAuthService },
        { provide: Router, useClass: MockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    // Get instances from the TestBed injector
    authService = TestBed.inject(AuthService) as unknown as MockAuthService;
    router = TestBed.inject(Router) as unknown as MockRouter;

    // Set initial state for authService mock if needed
    authService.setCurrentUser('test@mail.com', 'Tester'); // Example initial user

    fixture.detectChanges(); // Initial change detection
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render user email/username', () => {
    const userElement: HTMLElement = fixture.nativeElement.querySelector('.user-email'); // Adjust selector if needed
    expect(userElement).toBeTruthy();
    // Check for username first, then email as fallback (based on typical component logic)
    expect(userElement.textContent).toContain('Tester'); // Check for username

    // Test case when username is not available
    authService.setCurrentUser('only@mail.com', null);
    fixture.detectChanges();
    expect(userElement.textContent).toContain('only@mail.com'); // Check for email
  });


  it('should call logout and navigate on logout', () => {
    component.onLogout(); // Call the component's logout method
    expect(authService.logout).toHaveBeenCalled();
    // Check navigation AFTER logout observable completes (if navigation depends on it)
    // Since mock returns of(undefined), it completes synchronously
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
