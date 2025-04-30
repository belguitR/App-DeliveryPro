package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.responses.TourneeResponse;
import fr.uga.miage.l3.responses.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequestMapping("/api/tournees")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4300"})
public interface TourneeEndpoint {

    @Operation(summary = "Get the last Tournee by livreur email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tournee found"),
            @ApiResponse(responseCode = "404", description = "Tournee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/last/by-livreur/{email}")
    ResponseEntity<TourneeResponse> getLastTourneeByLivreurEmail(@PathVariable String email);
}
