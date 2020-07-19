export interface PanelDto {
  id: string;
  display: string;
  clientModel: string;
  online: boolean;
  hostId: string | null;
  assigned: boolean;
}
