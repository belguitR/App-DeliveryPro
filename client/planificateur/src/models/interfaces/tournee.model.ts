import { EtatTournee, EtatTourneeSchema } from '../enums/etat-tournee.enum';
import { Camion, camionSchema } from './camion.model';
import { z } from 'zod';

export interface Tournee {
  reference: string;
  etat: EtatTournee;
  lettre: string | null;
  distanceAParcourir: number | null;
  tempsAssemblageReel: number | null;
  equipeLivreursId: string | null;
  camion: Camion | null;
  journeeReference: string;
  commandeReferences: string[];
}

export const tourneeSchema: z.ZodType<Tournee> = z.object({
  reference: z.string(),
  etat: EtatTourneeSchema,
  lettre: z.string().nullable(),
  distanceAParcourir: z.number().nullable(),
  tempsAssemblageReel: z.number().nullable(),
  equipeLivreursId: z.string().nullable(),
  camion: camionSchema.nullable(),
  journeeReference: z.string(),
  commandeReferences: z.array(z.string()),
});

export interface TourneePostDto {
  reference: string;
  etat: EtatTournee;
  lettre: string | null;
  distanceAParcourir: number | null;
  tempsAssemblageReel: number | null;
  equipeLivreursId: string | null;
  camionCode: string | null;
  commandeReferences: string[];
  journeeReference: string;
}