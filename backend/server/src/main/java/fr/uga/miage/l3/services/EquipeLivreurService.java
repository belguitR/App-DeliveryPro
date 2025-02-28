package fr.uga.miage.l3.services;

import fr.uga.miage.l3.enums.StatusEquipeLivreur;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repository.EquipeLivreurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipeLivreurService {

    private final EquipeLivreurRepository equipeLivreurRepository;

    public List<EquipeLivreurEntity> getAllEquipes() {
        return equipeLivreurRepository.findAll();
    }

    @Transactional
    public EquipeLivreurEntity createEquipeLivreur(List<LivreurEntity> livreurs, Integer numEquipe) {
        if (livreurs.size() == 0 || livreurs.size() > 2) {
            throw new IllegalArgumentException("Une équipe doit contenir 1 ou 2 livreurs.");
        }

        Date today = new Date();
        boolean hasValidDriver = livreurs.stream()
                .anyMatch(livreur -> livreur.isAPermis() && livreur.getDateExpirationPermis() != null && livreur.getDateExpirationPermis().after(today));

        if (!hasValidDriver) {
            throw new IllegalArgumentException("Au moins un livreur doit avoir un permis valide.");
        }

        EquipeLivreurEntity equipeLivreur = EquipeLivreurEntity.builder()
                .numEquipe(numEquipe)
                .status(StatusEquipeLivreur.PRET) // Par défaut, prête
                .livreurs(livreurs)
                .build();

        return equipeLivreurRepository.save(equipeLivreur);
    }

    @Transactional
    public void affecterTournee(Integer numEquipe) {
        EquipeLivreurEntity equipeLivreur = equipeLivreurRepository.findById(numEquipe)
                .orElseThrow(() -> new EntityNotFoundException("Équipe non trouvée avec le numéro : " + numEquipe));

        equipeLivreur.setStatus(StatusEquipeLivreur.EN_LIVRAISON);
        equipeLivreurRepository.save(equipeLivreur);
    }

    @Transactional
    public void libererEquipe(Integer numEquipe) {
        EquipeLivreurEntity equipeLivreur = equipeLivreurRepository.findById(numEquipe)
                .orElseThrow(() -> new EntityNotFoundException("Équipe non trouvée avec le numéro : " + numEquipe));

        equipeLivreur.setStatus(StatusEquipeLivreur.PRET);
        equipeLivreurRepository.save(equipeLivreur);
    }


    @Transactional
    public EquipeLivreurEntity modifierEquipe(Integer idEquipe, EquipeLivreurEntity equipeLivreur) {
        EquipeLivreurEntity existingEquipe = equipeLivreurRepository.findById(idEquipe)
                .orElseThrow(() -> new EntityNotFoundException("Équipe non trouvée avec le numéro : " + idEquipe));

        existingEquipe.setLivreurs(equipeLivreur.getLivreurs());
        existingEquipe.setNumEquipe(equipeLivreur.getNumEquipe());

        return equipeLivreurRepository.save(existingEquipe);
    }


    @Transactional
    public void suprimeEquipe(Integer idEquipe) {
        if (!equipeLivreurRepository.existsById(idEquipe)) {
            throw new EntityNotFoundException("Équipe non trouvée avec le numéro : " + idEquipe);
        }
        equipeLivreurRepository.deleteById(idEquipe);
    }
}