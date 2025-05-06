import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GenericDialogComponent } from './generic-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';

describe('GenericDialogComponent', () => {
  let component: GenericDialogComponent;
  let fixture: ComponentFixture<GenericDialogComponent>;
  let dialogRefSpy: jasmine.SpyObj<MatDialogRef<GenericDialogComponent>>;

  const mockDialogData = {
    title: 'Delete Item',
    message: 'Are you sure you want to delete this?',
    confirmText: 'Delete',
    cancelText: 'Cancel'
  };

  beforeEach(async () => {
    dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      imports: [GenericDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MAT_DIALOG_DATA, useValue: mockDialogData }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GenericDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should display title and message', () => {
    const title = fixture.debugElement.query(By.css('h2')).nativeElement;
    const message = fixture.debugElement.query(By.css('mat-dialog-content')).nativeElement;

    expect(title.textContent).toContain('Delete Item');
    expect(message.textContent).toContain('Are you sure you want to delete this?');
  });

  it('should close dialog with false on cancel', () => {
    component.onCancel();
    expect(dialogRefSpy.close).toHaveBeenCalledWith(false);
  });

  it('should close dialog with true on confirm', () => {
    component.onConfirm();
    expect(dialogRefSpy.close).toHaveBeenCalledWith(true);
  });
});
