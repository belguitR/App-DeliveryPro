package fr.uga.miage.l3.services;

import fr.uga.miage.l3.enums.StatusEquipeLivreur;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repository.EquipeLivreurRepository;
import fr.uga.miage.l3.repository.LivreurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipeLivreurService {

    private final EquipeLivreurRepository equipeLivreurRepository;
    private final LivreurRepository livreurRepository;

    /**
     * Récupère toutes les équipes ; si aucune équipe n'est présente, lève "Aucune équipe trouvée."
     */
    public List<EquipeLivreurEntity> getAllEquipes() {
        List<EquipeLivreurEntity> equipes = equipeLivreurRepository.findAll();
        if (equipes.isEmpty()) {
            throw new EntityNotFoundException("Aucune équipe trouvée.");
        }
        return equipes;
    }

    /**
     * Crée une nouvelle équipe à partir d'une liste de 2 idEmploye.
     * Pour chaque id :
     *   - Vérifie que le livreur existe.
     *   - Vérifie que le livreur n'est pas déjà affecté (affecte == true).
     * Si l'un des livreurs est déjà affecté, lève une exception avec le message "le livreur {{idEmploye}} est déjà affecté".
     * Si la vérification réussit, marque les livreurs comme affectés, associe l'équipe et sauvegarde.
     */
    @Transactional
    public EquipeLivreurEntity createEquipeLivreur(List<String> idEmployes) {
        if (idEmployes.size() != 2) {
            throw new IllegalArgumentException("Une équipe doit contenir exactement 2 livreurs.");
        }

        // Récupérer les livreurs depuis la base
        List<LivreurEntity> existingLivreurs = livreurRepository.findAllByIdEmployeIn(idEmployes);
        if (existingLivreurs.size() != 2) {
            throw new IllegalArgumentException("Un ou plusieurs livreurs n'existent pas dans la base.");
        }

        // Pour éviter les modifications concurrentes, on travaille sur une copie de la liste
        List<LivreurEntity> livreursCopy = new ArrayList<>(existingLivreurs);
        for (LivreurEntity l : livreursCopy) {
            if (l.isAffecte()) {
                throw new IllegalArgumentException("le livreur " + l.getIdEmploye() + " est déjà affecté");
            }
            // Supprimer toute ancienne association
            l.setEquipeLivreur(null);
        }

        // Marquer chaque livreur comme affecté et assigner l'équipe (numEquipe sera auto-généré)
        for (LivreurEntity l : new ArrayList<>(livreursCopy)) {
            l.setAffecte(true);
            // On suppose que le numéro d'équipe est généré automatiquement en base,
            // donc on ne fixe pas explicitement numEquipe ici.
        }

        // Créer l'équipe avec la liste des livreurs
        EquipeLivreurEntity equipe = EquipeLivreurEntity.builder()
                .status(StatusEquipeLivreur.PRET)
                .livreurs(new ArrayList<>(livreursCopy))
                .build();

        // Associer l'équipe à chaque livreur (pour chaque copie, on ne modifie plus la liste originale)
        for (LivreurEntity l : new ArrayList<>(livreursCopy)) {
            l.setEquipeLivreur(equipe);
        }

        // Sauvegarder l'équipe, la cascade devrait gérer l'association aux livreurs
        equipeLivreurRepository.save(equipe);
        // Mettre à jour les livreurs en base
        livreurRepository.saveAll(new ArrayList<>(livreursCopy));

        return equipe;
    }

    /**
     * Supprime une équipe à partir de son numéro.
     * Pour chaque livreur de l'équipe, réinitialise son affectation.
     * Si l'équipe n'existe pas, lève une exception.
     */
    @Transactional
    public void supprimerEquipe(Integer numEquipe) {
        EquipeLivreurEntity equipe = equipeLivreurRepository.findById(numEquipe)
                .orElseThrow(() -> new EntityNotFoundException("ne peut pas supprimer cette équipe car elle n’existe pas"));
        List<LivreurEntity> affectes = livreurRepository.findAll().stream()
                .filter(l -> numEquipe.equals(l.getEquipeLivreur() != null ? l.getEquipeLivreur().getNumEquipe() : null))
                .collect(Collectors.toList());
        for (LivreurEntity l : affectes) {
            l.setAffecte(false);
            l.setEquipeLivreur(null);
            livreurRepository.save(l);
        }
        equipeLivreurRepository.delete(equipe);
    }

    @Transactional
    public EquipeLivreurEntity modifierEquipe(Integer numEquipe, List<String> livreursIds) {
        // Vérifier que l'équipe existe
        EquipeLivreurEntity equipe = equipeLivreurRepository.findById(numEquipe)
                .orElseThrow(() -> new EntityNotFoundException("equipe non trouvé"));

        // Vérifier que la liste contient exactement 2 identifiants
        if (livreursIds == null || livreursIds.size() != 2) {
            throw new IllegalArgumentException("Une équipe doit contenir exactement 2 livreurs.");
        }

        // Récupérer les livreurs depuis la base pour chaque id
        List<LivreurEntity> newLivreurs = livreursIds.stream()
                .map(id -> livreurRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("livreurs " + id + " non trouvé")))
                .collect(Collectors.toList());

        // Vérifier que chaque livreur n'est pas déjà affecté à une autre équipe
        for (LivreurEntity l : newLivreurs) {
            if (l.isAffecte() && (l.getEquipeLivreur() == null || !l.getEquipeLivreur().getNumEquipe().equals(numEquipe))) {
                String currentEquipe = (l.getEquipeLivreur() != null) ? l.getEquipeLivreur().getNumEquipe().toString() : "inconnue";
                throw new IllegalArgumentException("le livreur " + l.getIdEmploye() + " est deja affecte a l'equipe " + currentEquipe);
            }
        }

        // Désaffecter les livreurs actuellement associés à l'équipe qui ne sont pas dans la nouvelle liste
        if (equipe.getLivreurs() != null) {
            for (LivreurEntity oldLivreur : new ArrayList<>(equipe.getLivreurs())) {
                if (!newLivreurs.contains(oldLivreur)) {
                    oldLivreur.setAffecte(false);
                    oldLivreur.setEquipeLivreur(null);
                    livreurRepository.save(oldLivreur);
                }
            }
        }

        // Affecter les nouveaux livreurs à l'équipe
        for (LivreurEntity l : newLivreurs) {
            l.setAffecte(true);
            l.setEquipeLivreur(equipe);
            livreurRepository.save(l);
        }

        // Mettre à jour la liste des livreurs de l'équipe
        equipe.setLivreurs(new ArrayList<>(newLivreurs));

        return equipeLivreurRepository.save(equipe);
    }

}
