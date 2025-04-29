package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.ProduitEntity;
import fr.uga.miage.l3.repositories.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProduitComponent {

    private final ProduitRepository produitRepository;

    public List<ProduitEntity> findAll() {
        return produitRepository.findAll();
    }
    public Optional<ProduitEntity> findByReference(String reference) {
        return produitRepository.findById(reference);
    }
    public ProduitEntity save(ProduitEntity entity) {
         return produitRepository.save(entity);
    }
}
