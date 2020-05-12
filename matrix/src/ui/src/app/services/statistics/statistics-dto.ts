export interface StatisticsDto {
  timestamp: string;

  groupsConfigured: number;

  panelsConfigured: number;
  panelsAssigned: number;
  panelsUnassigned: number;
  panelsOnline: number;
  panelsOffline: number;

  devicesOnline: number;
  devicesProvisioned: number;
  devicesUnprovisioned: number;
}
