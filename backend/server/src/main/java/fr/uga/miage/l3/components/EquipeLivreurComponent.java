package fr.uga.miage.l3.components;

import fr.uga.miage.l3.exceptions.technicals.NotFoundEquipeLivreurEntityException;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.repository.EquipeLivreurRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EquipeLivreurComponent {

    private final EquipeLivreurRepository equipeLivreurRepository;

    public EquipeLivreurComponent(EquipeLivreurRepository equipeLivreurRepository) {
        this.equipeLivreurRepository = equipeLivreurRepository;
    }

    public List<EquipeLivreurEntity> getAllEquipes() {
        return equipeLivreurRepository.findAll();
    }

    public EquipeLivreurEntity getEquipeById(Integer numEquipe) throws NotFoundEquipeLivreurEntityException {
        return equipeLivreurRepository.findByNumEquipe(numEquipe).orElseThrow(()-> new NotFoundEquipeLivreurEntityException("L'équipe de livreur [" + numEquipe + "] n'a pas été trouvée"));
    }

    public EquipeLivreurEntity saveEquipe(EquipeLivreurEntity equipe) {
        return equipeLivreurRepository.save(equipe);
    }

    public void deleteEquipe(Integer numEquipe) {
        equipeLivreurRepository.deleteById(numEquipe);
    }
}