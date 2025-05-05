package fr.uga.miage.l3.responses;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourneeResponse {
    private String reference;
    private LocalDate date;
    private String planificateurEmail;
    private List<TourneeResponse> tournees;
}
