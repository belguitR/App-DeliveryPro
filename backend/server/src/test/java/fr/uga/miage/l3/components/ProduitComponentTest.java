package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.ProduitEntity;
import fr.uga.miage.l3.repositories.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProduitComponentTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitComponent produitComponent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProduit() {

        ProduitEntity produit1 = ProduitEntity
                .builder()
                .reference("P001")
                .titre("Produit 1")
                .description("Description 1")
                .prix(50.0)
                .optionMontage(true)
                .tdmTheorique(5)
                .build();

        ProduitEntity produit2 = ProduitEntity
                .builder()
                .reference("P002")
                .titre("Produit 2")
                .description("Description 2")
                .prix(10.0)
                .optionMontage(false)
                .tdmTheorique(0)
                .build();

        when(produitRepository.findAll()).thenReturn(List.of(produit1, produit2));

        List<ProduitEntity> result = produitComponent.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(produit1, produit2);
        verify(produitRepository).findAll();
    }

    @Test
    void testGetProduitByReference() {
        ProduitEntity produit = ProduitEntity
                .builder()
                .reference("P003")
                .titre("Produit 3")
                .description("Description 3")
                .prix(30.0)
                .optionMontage(true)
                .tdmTheorique(5)
                .build();

        when(produitRepository.findById("P003")).thenReturn(Optional.of(produit));

        Optional<ProduitEntity> result = produitComponent.findByReference("P003");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(produit);
        verify(produitRepository).findById("P003");
    }

    @Test
    void testFindByReference_shouldReturnEmptyIfNotFound() {
        when(produitRepository.findById("NOTFOUND")).thenReturn(Optional.empty());

        Optional<ProduitEntity> result = produitComponent.findByReference("NOTFOUND");

        assertThat(result).isEmpty();
        verify(produitRepository).findById("NOTFOUND");
    }

    @Test
    void testSaveProduit() {
        ProduitEntity produit = ProduitEntity
                .builder()
                .reference("P004")
                .titre("Produit 4")
                .description("Description 4")
                .prix(20.0)
                .optionMontage(false)
                .tdmTheorique(0)
                .build();

        when(produitRepository.save(produit)).thenReturn(produit);

        ProduitEntity result = produitComponent.save(produit);

        assertThat(result).isEqualTo(produit);
        verify(produitRepository).save(produit);
    }
}
