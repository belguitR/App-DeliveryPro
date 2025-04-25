package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.repositories.EquipeLivreursRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EquipeLivreursComponentTest {

    @Mock
    private EquipeLivreursRepository equipeLivreursRepository;

    @InjectMocks
    private EquipeLivreursComponent equipeLivreursComponent;

    public EquipeLivreursComponentTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_shouldCallRepository() {
        EquipeLivreursEntity entity = new EquipeLivreursEntity();
        when(equipeLivreursRepository.save(entity)).thenReturn(entity);

        EquipeLivreursEntity result = equipeLivreursComponent.save(entity);

        assertThat(result).isSameAs(entity);
        verify(equipeLivreursRepository).save(entity);
    }

    @Test
    void findAll_shouldReturnList() {
        List<EquipeLivreursEntity> list = List.of(new EquipeLivreursEntity());
        when(equipeLivreursRepository.findAll()).thenReturn(list);

        List<EquipeLivreursEntity> result = equipeLivreursComponent.findAll();

        assertThat(result).isEqualTo(list);
        verify(equipeLivreursRepository).findAll();
    }

    @Test
    void findById_shouldReturnOptional() {
        EquipeLivreursEntity entity = new EquipeLivreursEntity();
        when(equipeLivreursRepository.findById(1)).thenReturn(Optional.of(entity));

        Optional<EquipeLivreursEntity> result = equipeLivreursComponent.findById(1);

        assertThat(result).contains(entity);
        verify(equipeLivreursRepository).findById(1);
    }

    @Test
    void deleteById_shouldCallRepository() {
        equipeLivreursComponent.deleteById(1);
        verify(equipeLivreursRepository).deleteById(1);
    }
}
