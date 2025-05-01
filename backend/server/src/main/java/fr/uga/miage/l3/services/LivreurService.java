package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.LivreurComponent;
import fr.uga.miage.l3.exceptions.technical.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.LivreurMapper;
import fr.uga.miage.l3.models.EntrepotEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repositories.EquipeLivreursRepository;
import fr.uga.miage.l3.repositories.EntrepotRepository;
import fr.uga.miage.l3.responses.LivreurResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LivreurService {
    private final LivreurComponent livreurComponent;
    private final EquipeLivreursRepository equipeRepo;
    private final EntrepotRepository entrepotRepository;

    public List<LivreurResponse> getAllLivreurs() {
        return livreurComponent.findAll().stream()
                .map(LivreurMapper::toResponse)
                .collect(Collectors.toList());
    }

    public LivreurResponse getLivreurByEmail(String email) {
        LivreurEntity entity = livreurComponent.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Livreur non trouvé: " + email));
        return LivreurMapper.toResponse(entity);
    }

    public LivreurResponse updateLivreur(String email, LivreurResponse livreur) {
        LivreurEntity entity = livreurComponent.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Livreur non trouvé: " + email));
        entity.setNom(livreur.getNom());
        entity.setPrenom(livreur.getPrenom());
        entity.setAffecte(livreur.isAffecte());
        entity.setAPermis(livreur.isAPermis());
        entity.setPhoto(livreur.getPhoto());
        entity.setTelephone(livreur.getTelephone());

        if (livreur.getEquipeId() != null) {
            entity.setEquipeLivreurs(
                    equipeRepo.findById(livreur.getEquipeId()).orElse(null)
            );
        } else {
            entity.setEquipeLivreurs(null);
        }

        if (livreur.getEntrepotNom() != null) {
            EntrepotEntity entrepot = entrepotRepository.findById(livreur.getEntrepotNom()).orElse(null);
            entity.setEntrepot(entrepot);
        } else {
            entity.setEntrepot(null);
        }

        LivreurEntity saved = livreurComponent.save(entity);
        return LivreurMapper.toResponse(saved);
    }
}
