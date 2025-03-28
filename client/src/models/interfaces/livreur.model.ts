import { Employe } from './employe.model';
import { Agenda } from './agenda.model';
import { Entrepot } from './entrepot.model';
export interface Livreur extends Employe {
  apermis: boolean;
  affecte: boolean;
  entrepot?: Entrepot;
}