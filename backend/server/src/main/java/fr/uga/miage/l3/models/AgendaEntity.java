package fr.uga.miage.l3.models;

import jakarta.persistence.*;

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
@Table(name = "agenda")
public class AgendaEntity {
    @Id
    @Column(nullable = false)
    private String idAgenda;

    @OneToMany(mappedBy = "agenda")
    private Set<CreneauEntity> creneaux;
}
