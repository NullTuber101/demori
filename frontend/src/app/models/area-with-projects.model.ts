// area-with-projects.model.ts
import { Area } from './area.model';
import { Project } from './project.model';

export interface AreaWithProjects extends Area {
  projects: Project[];  // Add the projects property here
}
