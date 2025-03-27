package fr.uga.miage.l3.components;

import fr.uga.miage.l3.exceptions.technicals.NotFoundCommandeEntityException;
import fr.uga.miage.l3.exceptions.technicals.NotFoundEquipeLivreurEntityException;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.repository.CommandeRepository;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CommandeComponentTest {
    @Autowired
    private CommandeComponent commandeComponent;
    @MockBean
    private CommandeRepository commandeRepository;

    @Test
    void getAllCommandes() {
        // GIVEN
        CommandeEntity commande1 = CommandeEntity.builder().reference(50).build();
        CommandeEntity commande2 = CommandeEntity.builder().reference(51).build();
        CommandeEntity commande3 = CommandeEntity.builder().reference(52).build();
        CommandeEntity commande4 = CommandeEntity.builder().reference(53).build();

        Set<Integer> commandsRef = new HashSet<>();
        commandsRef.add(commande1.getReference());
        commandsRef.add(commande2.getReference());
        commandsRef.add(commande3.getReference());
        commandsRef.add(commande4.getReference());

        //WHEN
        when(commandeRepository.findAll()).thenReturn(List.of(commande1, commande2, commande3, commande4));
        List<CommandeEntity> commandRefList = commandeComponent.getAllCommandes();

        // THEN
        assertThat(commandRefList.size()).isEqualTo(4);
        assertThat(commandsRef.stream().findFirst()).isEqualTo(commandsRef.stream().findFirst());
    }

    @Test
    void testGetCommandeById() throws NotFoundCommandeEntityException {
        // GIVEN
        CommandeEntity c = CommandeEntity.builder().reference(50).build();

        // WHEN
        when(commandeRepository.findById(50)).thenReturn(Optional.of(c));
        CommandeEntity commandeFound = commandeComponent.getCommandeById(50);

        // THEN
        assertThat(commandeFound.getReference()).isEqualTo(50);
    }

    @Test
    void getCommandeNotOk(){
        //Given
        when(commandeRepository.findById(1)).thenReturn(Optional.empty());
        //then - when
        assertThrows(NotFoundCommandeEntityException.class,()->commandeComponent.getCommandeById(50));
    }
}
