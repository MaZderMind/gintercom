export interface StatisticsDto {
  timestamp: string;

  groupsConfigured: number;

  panelsConfigured: number;
  panelsAssigned: number;
  panelsUnassigned: number;
  panelsOnline: number;
  panelsOffline: number;

  clientsOnline: number;
  clientsProvisioned: number;
  clientsUnprovisioned: number;
}
