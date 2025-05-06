import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavbarComponent } from './navbar.component';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavbarComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should set isSuperUser to true when token has SUPER_USER role', () => {
    const payload = { role: 'SUPER_USER' };
    const token = createMockJWT(payload);
    localStorage.setItem('token', token);

    component.ngOnInit();
    expect(component.isSuperUser).toBeTrue();
  });

  it('should set isSuperUser to false when role is not SUPER_USER', () => {
    const payload = { role: 'EDITOR' };
    const token = createMockJWT(payload);
    localStorage.setItem('token', token);

    component.ngOnInit();
    expect(component.isSuperUser).toBeFalse();
  });

  it('should set isSuperUser to false if token is malformed', () => {
    localStorage.setItem('token', 'invalid.token.structure');
    component.ngOnInit();
    expect(component.isSuperUser).toBeFalse();
  });

  // Helper to create mock JWT tokens
  function createMockJWT(payload: any): string {
    const base64 = btoa(JSON.stringify(payload));
    return `header.${base64}.signature`;
  }
});
