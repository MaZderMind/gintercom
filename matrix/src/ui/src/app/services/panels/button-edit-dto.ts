import {CommunicationTargetType} from 'src/app/services/communication-target-type';

export interface ButtonEditDto {
  id: string;
  display: string | null;
  target: string;
  targetType: CommunicationTargetType;
}
