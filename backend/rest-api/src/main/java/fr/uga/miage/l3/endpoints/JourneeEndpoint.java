package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.responses.JourneeResponse;
import fr.uga.miage.l3.requests.JourneeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/journees")
public interface JourneeEndpoint {

    @Operation(summary = "Get all journees")
    @ApiResponse(responseCode = "200", description = "List of journees")
    @GetMapping
    ResponseEntity<List<JourneeResponse>> getAllJournees();

    @Operation(summary = "Get a journee by reference")
    @ApiResponse(responseCode = "200", description = "Journee found")
    @GetMapping("/{reference}")
    ResponseEntity<JourneeResponse> getJourneeByReference(@PathVariable String reference);

    @Operation(summary = "Create a new journee")
    @ApiResponse(responseCode = "201", description = "Journee created")
    @PostMapping
    ResponseEntity<JourneeResponse> createJournee(@RequestBody JourneeRequest request);

    @Operation(summary = "Delete a journee by reference")
    @ApiResponse(responseCode = "204", description = "Journee deleted")
    @DeleteMapping("/{reference}")
    ResponseEntity<Void> deleteJournee(@PathVariable String reference);
}
