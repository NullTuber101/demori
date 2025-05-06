import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AlertComponent, AlertData } from './alert.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('AlertComponent', () => {
  let component: AlertComponent;
  let fixture: ComponentFixture<AlertComponent>;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<AlertComponent>>;

  const mockData: AlertData = {
    title: 'Error Occurred',
    message: 'Something went wrong.'
  };

  beforeEach(async () => {
    mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      imports: [AlertComponent, NoopAnimationsModule],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: mockData },
        { provide: MatDialogRef, useValue: mockDialogRef }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should display the alert title and message', () => {
    const titleEl = fixture.debugElement.query(By.css('.alert-title')).nativeElement;
    const messageEl = fixture.debugElement.query(By.css('.alert-message')).nativeElement;

    expect(titleEl.textContent).toContain(mockData.title);
    expect(messageEl.textContent).toContain(mockData.message);
  });

  it('should close the dialog when OK is clicked', () => {
    const button = fixture.debugElement.query(By.css('button')).nativeElement;
    button.click();
    expect(mockDialogRef.close).toHaveBeenCalled();
  });
});
