import {ButtonEditDto} from 'src/app/services/panels/button-edit-dto';

export interface PanelEditDto {
  id: string;
  display: string;
  clientId: string | null;

  rxGroups: string[];
  txGroups: string[];

  buttonSets: string[];
  buttons: ButtonEditDto[];
}
