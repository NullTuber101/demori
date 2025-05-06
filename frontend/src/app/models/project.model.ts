import { Status } from './status.model';  // Assuming Status is in a separate file
import { Area } from './area.model';  // Assuming Area is in a separate file

export interface Project {
  id: number;
  projectName: string;
  description: string;
  developer: string;
  jira: string;
  startDate: string | Date;
  endDate: string | Date;
  area: Area;  // Using the Area model
  status: Status;  // Using the Status model
}
