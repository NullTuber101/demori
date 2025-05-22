import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'area-manager',
    loadComponent: () =>
      import('./area-manager/area-manager.component').then(
        (m) => m.AreaManagerComponent
      )
  },
  {
    path: 'status-manager',
    loadComponent: () =>
      import('./status-manager/status-manager.component').then(
        (m) => m.StatusManagerComponent
      )
  },
  {
    path: 'project-add',
    loadComponent: () =>
      import('./project-add/project-add.component').then(
        (m) => m.ProjectAddComponent
      )
  },
  {
    path: 'edit-project/:id',
    loadComponent: () =>
      import('./project-edit/project-edit.component').then(
        (m) => m.ProjectEditComponent
      )
  },
    {
      path: '',
      loadComponent: () =>
        import('./project-view/project-view.component').then(
          (m) => m.ProjectViewComponent
        )
    },
  {
    path: 'plot-section',
    loadComponent: () =>
      import('./plots/plots.component').then(
        (m) => m.PlotsComponent
      )
  },
  {
    path: 'project/:id/insight',
    loadComponent: () =>
      import('./project-wise-insight/project-wise-insight.component').then(
        (m) => m.ProjectWiseInsightComponent
      )
  },
  {
    path: 'scrum-section',
    loadComponent: () =>
      import('./velocity-section/velocity-section.component').then(
        (m) => m.VelocitySectionComponent   
      )
  }
];
