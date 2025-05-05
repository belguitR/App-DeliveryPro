import { Injectable, inject } from '@angular/core';
import { LatLng } from 'leaflet';
import { DataService } from './data.service';
import { CartoService } from './carto.service';
import { KMeansService } from './kmeans.service';
import { EquipeLivreurs } from '../../models/interfaces/equipe-livreurs.model';
import { Commande } from '../../models/interfaces/commande.model';
import { EtatTournee } from '../../models/enums/etat-tournee.enum';
import { Camion } from '../../models/interfaces/camion.model';
import { Tournee, TourneePostDto } from '../../models/interfaces/tournee.model';
import { Journee, JourneePostDto } from '../../models/interfaces/journee.model';
import { firstValueFrom } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PlanificationService {
  private dataService = inject(DataService);
  private cartoService = inject(CartoService);
  private kmeansService = inject(KMeansService);

  getTomorrowDate(): string {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    return d.toISOString().substring(0, 10);
  }

  getEquipeColor(id: number): string {
    return this.cartoService.getColorForTeam(id);
  }

  private genRef(prefix: string, items: { reference: string }[], pad: number): string {
    if (!items.length) return `${prefix}${'1'.padStart(pad, '0')}`;
    const nums = items
      .map(i => parseInt(i.reference.replace(prefix, ''), 10))
      .filter(n => !isNaN(n));
    const max = Math.max(...nums);
    return `${prefix}${(max + 1).toString().padStart(pad, '0')}`;
  }

  generateJourneeReference(journees: Journee[]): string {
    return this.genRef('JRN', journees, 8);
  }

  generateTourneeReference(tournees: Tournee[]): string {
    return this.genRef('TRN', tournees, 3);
  }

  async creerTournees(
    equipes: EquipeLivreurs[],
    commandes: Commande[],
    entrepot: LatLng,
    strategy: 'balanced' | 'custom',
    date: string,
    camions: Camion[],
    customSizes?: number[],
    

  ) {
    const colorMap = new Map<number, string>();
    const routes: LatLng[][] = [];
    const journees = this.dataService.journees();
    const jourRef = this.generateJourneeReference(journees);
    const existing = journees.flatMap(j => j.tournees ?? []);
    const tournees: Tournee[] = [];

    const coords = commandes.map(c => [c.client.coords.lat, c.client.coords.lng]);
    const { clusters } = this.kmeansService.kmeans(coords, equipes.length, strategy, customSizes);

    const raw: Commande[][] = Array.from({ length: equipes.length }, () => []);
    commandes.forEach((c, i) => {
      const cid = clusters[i];
      if (cid != null) raw[cid].push(c);
    });

    for (let i = 0; i < raw.length; i++) {
      const cluster = raw[i];
      const equipe = equipes[i];
      if (!cluster.length) continue;

      const trnRef = this.generateTourneeReference([...existing, ...tournees]);
      const resp = await this.cartoService.getOptimizationAutmatique(cluster, [equipe], entrepot).toPromise();
      if (!resp?.routes?.length) continue;

      const route = resp.routes[0];
      const path = route.steps.map((s:any) => new LatLng(s.location[1], s.location[0]));
      const distanceKm = await this.cartoService.getDistanceFromORS(path);
      const commandesCount = path.length - 2;  
      const travelTimeMin    = (distanceKm / 40) * 60;
      const deliveryTimeMin  = commandesCount * 10;
      const totalTimeMin     = Math.round(travelTimeMin + deliveryTimeMin);
      const refs = route.steps
        .filter((s:any) => s.type === 'job')
        .map((s:any) => cluster[s.job - 1].reference);

      routes.push(path);
      colorMap.set(equipe.idEquipe, this.getEquipeColor(equipe.idEquipe));
      const stats: { equipeId: number; distanceKm: number; timeMin: number }[] = [];


      tournees.push({
        reference: trnRef,
        etat: EtatTournee.PLANIFIEE,
        lettre: String.fromCharCode(65 + i),
        distanceAParcourir: Math.round(distanceKm),
        tempsAssemblageReel: totalTimeMin,
        equipeLivreursId: String(equipe.idEquipe),
        camion: camions[i],
        commandeReferences: refs,
        journeeReference: jourRef
      });
    }

    const dto: TourneePostDto[] = tournees.map(t => ({
      reference: t.reference,
      etat: t.etat,
      lettre: t.lettre,
      distanceAParcourir: t.distanceAParcourir,
      tempsAssemblageReel: t.tempsAssemblageReel,
      equipeLivreursId: t.equipeLivreursId,
      camionCode: t.camion?.code ?? null,
      commandeReferences: t.commandeReferences,
      journeeReference: t.journeeReference
    }));

    const post: JourneePostDto = {
      reference: jourRef,
      date,
      planificateurEmail: this.dataService.planificateurEmail(),
      tournees: dto
    };
    await firstValueFrom(this.dataService.createJournee(post));

    return { routes, teamIdToColor: colorMap };
  }

  async loadTournees(
    date: string,
    entrepot: LatLng
  ): Promise<{ routes: LatLng[][]; teamIdToColor: Map<number,string>; equipes: EquipeLivreurs[]; remaining: Commande[]; loaded: Commande[] }> {
    const commandes = this.dataService.commandes();
    const used = this.dataService.journees().flatMap(j => j.tournees?.flatMap(t => t.commandeReferences) ?? []);
    const jour = this.dataService.journees().find(j => j.date?.startsWith(date));
    if (!jour) {
      return { routes: [], teamIdToColor: new Map(), equipes: [], remaining: commandes.filter(c => !used.includes(c.reference)), loaded: [] };
    }

    const mapEq = new Map<number,EquipeLivreurs>(this.dataService.equipesLivreurs().map(e => [e.idEquipe, e]));
    const routes: LatLng[][] = [];
    const colors = new Map<number,string>();
    const equipes: EquipeLivreurs[] = [];
    const loaded: Commande[] = [];

    for (const t of jour.tournees ?? []) {
      const cmds = t.commandeReferences
        .map(r => commandes.find(c => c.reference === r))
        .filter((c): c is Commande => !!c);
      const eq = mapEq.get(+t.equipeLivreursId!);
      if (!eq) continue;

      loaded.push(...cmds);
      const resp = await this.cartoService.getOptimizationAutmatique(cmds, [eq], entrepot).toPromise();
      const path = resp.routes[0].steps.map((s:any) => new LatLng(s.location[1], s.location[0]));
      routes.push(path);
      colors.set(eq.idEquipe, this.getEquipeColor(eq.idEquipe));
      equipes.push(eq);
    }

    return {
      routes,
      teamIdToColor: colors,
      equipes,
      remaining: commandes.filter(c => !used.includes(c.reference)),
      loaded
    };
  }

  async checkIfJourneeExists(date: string): Promise<boolean> {
    this.dataService.refreshJournees();
    await firstValueFrom(this.dataService.isDataLoaded$);
    return this.dataService.journees().some(j => j.date?.startsWith(date));
  }
  
}