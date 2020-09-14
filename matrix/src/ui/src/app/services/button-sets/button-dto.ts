import {CommunicationTargetType} from 'src/app/services/communication-target-type';

export interface ButtonDto {
  id: string;
  display: string;
  target: string;
  targetType: CommunicationTargetType;
}
