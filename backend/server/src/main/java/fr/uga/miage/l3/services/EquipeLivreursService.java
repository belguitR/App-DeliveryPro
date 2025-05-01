package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.EquipeLivreursComponent;
import fr.uga.miage.l3.exceptions.technical.BusinessRuleException;
import fr.uga.miage.l3.exceptions.technical.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.EquipeLivreursMapper;
import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.enums.StatusEquipeLivreurs;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repositories.LivreurRepository;
import fr.uga.miage.l3.responses.EquipeLivreursResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class EquipeLivreursService {
    private final EquipeLivreursComponent equipeComponent;
    private final LivreurRepository livreurRepository;

    public List<EquipeLivreursResponse> getAllEquipes() {
        return equipeComponent.findAll().stream()
                .map(EquipeLivreursMapper::toResponse)
                .collect(Collectors.toList());
    }

    public EquipeLivreursResponse createEquipe(EquipeLivreursResponse response) {
        List<String> livreurIds = response.getLivreurIds();

        if (livreurIds == null || livreurIds.size() < 1 || livreurIds.size() > 2) {
            throw new BusinessRuleException("Equipe must have 1 or 2 livreurs");
        }

        Set<LivreurEntity> livreurs = new HashSet<>();
        boolean atLeastOnePermis = false;

        for (String trigramme : livreurIds) {
            LivreurEntity livreur = livreurRepository.findById(trigramme)
                    .orElseThrow(() -> new ResourceNotFoundException("Livreur not found: " + trigramme));

            if (livreur.getEquipeLivreurs() != null) {
                throw new BusinessRuleException("Livreur " + trigramme + " is already in an équipe");
            }

            livreurs.add(livreur);
            if (livreur.isAPermis()) {
                atLeastOnePermis = true;
            }
        }

        if (!atLeastOnePermis) {
            throw new BusinessRuleException("At least one livreur must have permis");
        }

        EquipeLivreursEntity entity = new EquipeLivreursEntity();
        entity.setStatus(StatusEquipeLivreurs.valueOf(response.getStatus()));
        entity.setLivreurs(livreurs);

        EquipeLivreursEntity savedEquipe = equipeComponent.save(entity);

        for (LivreurEntity livreur : livreurs) {
            livreur.setEquipeLivreurs(savedEquipe);
            livreur.setAffecte(true);
            livreurRepository.save(livreur);
        }

        List<String> savedLivreurIds = savedEquipe.getLivreurs() != null
                ? savedEquipe.getLivreurs().stream()
                .map(LivreurEntity::getTrigramme)
                .collect(Collectors.toList())
                : List.of();

        return new EquipeLivreursResponse(
                savedEquipe.getIdEquipe(),
                savedEquipe.getStatus().name(),
                savedLivreurIds
        );
    }

    public void deleteEquipe(Integer id) {
        EquipeLivreursEntity equipe = equipeComponent.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe not found: " + id));
        List<LivreurEntity> livreurs = livreurRepository.findByEquipeLivreurs(equipe);
        for (LivreurEntity livreur : livreurs) {
            livreur.setEquipeLivreurs(null);
            livreur.setAffecte(false);
            livreurRepository.save(livreur);
        }
        equipeComponent.deleteById(id);
    }

    public EquipeLivreursResponse patchEquipeStatus(Integer id, String status) {
        EquipeLivreursEntity entity = equipeComponent.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe not found: " + id));
        entity.setStatus(StatusEquipeLivreurs.valueOf(status));
        EquipeLivreursEntity saved = equipeComponent.save(entity);
        return EquipeLivreursMapper.toResponse(saved);
    }
}
