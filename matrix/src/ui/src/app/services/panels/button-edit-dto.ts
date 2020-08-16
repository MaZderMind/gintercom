export interface ButtonEditDto {
  id: string;
  display: string | null;
  target: string;
  targetType: 'GROUP' | 'PANEL';
}
