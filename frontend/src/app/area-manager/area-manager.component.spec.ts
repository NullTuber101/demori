import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { AreaManagerComponent } from './area-manager.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { AreaService } from '../services/area.service';

describe('AreaManagerComponent', () => {
  let component: AreaManagerComponent;
  let fixture: ComponentFixture<AreaManagerComponent>;
  let areaServiceSpy: jasmine.SpyObj<AreaService>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  const mockAreas = [
    { id: 1, name: 'Area A', leadName: 'Alice', leadEmail: 'alice@test.com' },
    { id: 2, name: 'Area B', leadName: 'Bob', leadEmail: 'bob@test.com' }
  ];

  beforeEach(async () => {
    areaServiceSpy = jasmine.createSpyObj('AreaService', ['getAreas', 'addArea', 'updateArea', 'deleteArea']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, MatSnackBarModule],
      declarations: [AreaManagerComponent],
      providers: [
        { provide: AreaService, useValue: areaServiceSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AreaManagerComponent);
    component = fixture.componentInstance;
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load areas on init', () => {
    areaServiceSpy.getAreas.and.returnValue(of(mockAreas));
    component.ngOnInit();
    expect(areaServiceSpy.getAreas).toHaveBeenCalled();
    expect(component.areas.length).toBe(2);
  });

  it('should add area if form is valid', () => {
    const newArea = { id: 3, name: 'Area C', leadName: 'Clara', leadEmail: 'clara@test.com' };
    areaServiceSpy.addArea.and.returnValue(of(newArea));
    component.areaForm.setValue({ name: 'Area C', leadName: 'Clara', leadEmail: 'clara@test.com' });

    component.addArea();
    expect(areaServiceSpy.addArea).toHaveBeenCalledWith(newArea);
    expect(component.areas).toContain(newArea);
  });

  it('should not add area if form is invalid', () => {
    component.areaForm.setValue({ name: '', leadName: '', leadEmail: '' });
    component.addArea();
    expect(areaServiceSpy.addArea).not.toHaveBeenCalled();
  });

  it('should enable edit and populate form', () => {
    component.areas = mockAreas;
    component.enableEdit(1);
    expect(component.editIndex).toBe(1);
    expect(component.editForm.value.name).toBe('Area B');
  });

  it('should save edit if form is valid', () => {
    const updatedArea = { id: 2, name: 'Updated B', leadName: 'Updated Bob', leadEmail: 'bob@test.com' };
    component.areas = mockAreas;
    component.editIndex = 1;
    component.editForm.setValue(updatedArea);
    areaServiceSpy.updateArea.and.returnValue(of(updatedArea));

    component.saveEdit(1);
    expect(areaServiceSpy.updateArea).toHaveBeenCalledWith(updatedArea);
    expect(component.areas[1].name).toBe('Updated B');
    expect(component.editIndex).toBeNull();
  });

  it('should cancel edit', () => {
    component.editIndex = 0;
    component.cancelEdit();
    expect(component.editIndex).toBeNull();
  });

  it('should confirm and delete area', fakeAsync(() => {
  component.areas = [...mockAreas];
  dialogSpy.open.and.returnValue({ afterClosed: () => of(true) } as any);
  areaServiceSpy.deleteArea.and.returnValue(of(void 0)); // 🔧 Fixed here

  component.confirmDelete(0);
  tick();

  expect(areaServiceSpy.deleteArea).toHaveBeenCalledWith(1);
  expect(component.areas.length).toBe(1);
  expect(component.areas[0].name).toBe('Area B');
}));
});
