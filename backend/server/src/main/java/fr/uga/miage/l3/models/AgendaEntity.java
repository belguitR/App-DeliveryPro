package fr.uga.miage.l3.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;
import java.util.Set;

import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AgendaEntity {
    @Id
    @Column(nullable = false)
    private String idAgenda;

    @OneToMany(mappedBy = "agenda")
    private Set<CreneauEntity> creneaux;
}
