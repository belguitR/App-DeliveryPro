package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.repository.EquipeLivreurRepository;
import fr.uga.miage.l3.exceptions.technicals.NotFoundEquipeLivreurEntityException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class EquipeLivreurComponentTest {
    @Autowired
    private EquipeLivreurComponent equipeLivreurComponent;
    @MockBean
    private EquipeLivreurRepository equipeLivreurRepository;

    @Test
    void testGetAllEquipes() {
        // GIVEN
        EquipeLivreurEntity equipe1 = EquipeLivreurEntity.builder().numEquipe(1).build();
        EquipeLivreurEntity equipe2 = EquipeLivreurEntity.builder().numEquipe(2).build();
        EquipeLivreurEntity equipe3 = EquipeLivreurEntity.builder().numEquipe(3).build();
        EquipeLivreurEntity equipe4 = EquipeLivreurEntity.builder().numEquipe(4).build();

        Set<Integer> equipesNum = new HashSet<>();
        equipesNum.add(equipe1.getNumEquipe());
        equipesNum.add(equipe2.getNumEquipe());
        equipesNum.add(equipe3.getNumEquipe());
        equipesNum.add(equipe4.getNumEquipe());
        // WHEN
        when(equipeLivreurRepository.findAll()).thenReturn(List.of(equipe1, equipe2, equipe3, equipe4));
        List<EquipeLivreurEntity> equipesNumList = equipeLivreurComponent.getAllEquipes();

        // THEN
        assertThat(equipesNumList.size()).isEqualTo(4);
        assertThat(equipesNum.stream().findFirst()).isEqualTo(equipesNum.stream().findFirst());
    }

    @Test
    void testGetEquipeById() throws NotFoundEquipeLivreurEntityException {
        // GIVEN
        EquipeLivreurEntity equipe = EquipeLivreurEntity.builder().numEquipe(1).build();

        // WHEN
        when(equipeLivreurRepository.findByNumEquipe(1)).thenReturn(Optional.of(equipe));
        EquipeLivreurEntity equipeTrouver = equipeLivreurComponent.getEquipeById(1);

        // THEN
        assertThat(equipeTrouver.getNumEquipe()).isEqualTo(1);
    }

    @Test
    void getEquipeLivreurNotOk(){
        // GIVEN
        when(equipeLivreurRepository.findById(1)).thenReturn(Optional.empty());
        // THEN - WHEN
        assertThrows(NotFoundEquipeLivreurEntityException.class, () -> equipeLivreurComponent.getEquipeById(1));
    }
}
