import { Injectable } from '@angular/core';
import { squaredEuclidean } from 'ml-distance-euclidean';

@Injectable({
  providedIn: 'root',
})
export class KMeansService {

  /**
 * L'algorithme K-means vise à partitionner n observations en k clusters, dans lesquels chaque observation appartient au cluster dont la moyenne est la plus proche.
 * Cette implémentation est basée sur le package `ml-kmeans` (https://www.npmjs.com/package/ml-kmeans),
 *  avec la modification suivante : un nombre maximum de points par cluster est imposé.
 * 
 * L'algorithme K-means original a été pris de ml-kmeans :
 * https://www.npmjs.com/package/ml-kmeans
 * 
 * Modifications :
 * - Assure que chaque cluster contient au maximum un nombre spécifié de points (`maxPerCluster`).
 * - L'algorithme s'adapte à cette contrainte tout en partitionnant les données.
 */
  kmeans(
    data: number[][],
    K: number,
    maxPerCluster = 50,
    maxIterations = 100,
    tolerance = 1e-6
  ) { 
    const distance = squaredEuclidean;

    let centers = [...data.slice(0, K)];
    const clusterID = new Array<number>(data.length).fill(-1);
    let converged = false;
    let iterations = 0;

    while (!converged && iterations < maxIterations) {
      const clusterSizes = new Array(K).fill(0);

      for (let i = 0; i < data.length; i++) {
        let minDist = Infinity;
        let chosenCluster = -1;

        for (let j = 0; j < K; j++) {
          if (clusterSizes[j] >= maxPerCluster) continue;

          const dist = distance(data[i], centers[j]);
          if (dist < minDist) {
            minDist = dist;
            chosenCluster = j;
          }
        }

        if (chosenCluster === -1) {
          continue;
        }

        clusterID[i] = chosenCluster;
        clusterSizes[chosenCluster]++;
      }

      const newCenters = Array.from({ length: K }, () => new Array(data[0].length).fill(0));
      const counts = new Array(K).fill(0);

      for (let i = 0; i < data.length; i++) {
        const cluster = clusterID[i];
        if (cluster === -1) continue;

        counts[cluster]++;
        for (let d = 0; d < data[i].length; d++) {
          newCenters[cluster][d] += data[i][d];
        }
      }

      for (let j = 0; j < K; j++) {
        for (let d = 0; d < newCenters[j].length; d++) {
          newCenters[j][d] /= counts[j] || 1;
        }
      }

      
      converged = centers.every((c, i) => distance(c, newCenters[i]) <= tolerance);
      centers = newCenters;
      iterations++;
    }
    console.log({
      clusters: clusterID,
      centroids: centers,
      iterations,
      converged,
    });
    
    const balancedClusterIDs = this.balanceClusters(data, clusterID, K);

    return {
      clusters: balancedClusterIDs,
      centroids: centers,
      iterations,
      converged,
    };
  }

  private balanceClusters(
    data: number[][],
    clusters: number[],
    K: number
  ): number[] {
    const squaredDistance = squaredEuclidean;
    const clusterPoints: number[][][] = Array.from({ length: K }, () => []);
    const assignments: number[] = [...clusters];
  
    for (let i = 0; i < data.length; i++) {
      const cid = assignments[i];
      if (cid !== -1) {
        clusterPoints[cid].push(data[i]);
      }
    }
  
    let changed = true;
  
    while (changed) {
      changed = false;
      const sorted = clusterPoints
        .map((points, idx) => ({ idx, size: points.length }))
        .sort((a, b) => b.size - a.size);
  
      const largest = sorted[0];
      const smallest = sorted[sorted.length - 1];
  
      if (largest.size - smallest.size <= 1) break;
  
      const donorClusterIdx = largest.idx;
      const recipientClusterIdx = smallest.idx;
  
      let bestCandidateIdx = -1;
      let bestDist = Infinity;
  
      for (let i = 0; i < data.length; i++) {
        if (assignments[i] !== donorClusterIdx) continue;
  
        const candidatePoint = data[i];
  
        for (const targetPoint of clusterPoints[recipientClusterIdx]) {
          const dist = squaredDistance(candidatePoint, targetPoint);
          if (dist < bestDist) {
            bestDist = dist;
            bestCandidateIdx = i;
          }
        }
      }
  
      if (bestCandidateIdx !== -1) {
        const point = data[bestCandidateIdx];
        clusterPoints[donorClusterIdx] = clusterPoints[donorClusterIdx].filter(
          p => p !== point
        );
        clusterPoints[recipientClusterIdx].push(point);
        assignments[bestCandidateIdx] = recipientClusterIdx;
  
        changed = true;
      } else {
        break;
      }
    }
  
    return assignments;
  }
  
  
}
