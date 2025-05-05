package fr.uga.miage.l3.requests;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourneeRequest {
    private String reference;
    private LocalDate date;
    private String planificateurEmail;
    private Double coutTotal;
    private List<TourneeRequest> tournees;
}
