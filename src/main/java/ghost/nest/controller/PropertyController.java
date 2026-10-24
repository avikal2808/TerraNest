package ghost.nest.controller;

import ghost.nest.dto.*;
import ghost.nest.service.LivabilityService;
import ghost.nest.service.PropertyService;
import ghost.nest.service.RoiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final LivabilityService livabilityService;
    private final RoiService roiService;

    @PostMapping
    public ResponseEntity<PropertyResponseDTO> createProperty(@Valid @RequestBody PropertyRequestDTO request) {
        PropertyResponseDTO response = propertyService.createProperty(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponseDTO> getProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PropertyResponseDTO>> searchProperties(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) Double radius) {
        return ResponseEntity.ok(propertyService.searchPropertiesByLocation(lat, lng, radius));
    }

    @GetMapping("/{id}/livability")
    public ResponseEntity<LivabilityResponseDTO> getLivabilityScore(@PathVariable Long id) {
        return ResponseEntity.ok(livabilityService.calculateLivability(id));
    }

    @GetMapping("/{id}/roi")
    public ResponseEntity<RoiResponseDTO> getRoiPrediction(@PathVariable Long id) {
        return ResponseEntity.ok(roiService.predictRoi(id));
    }
}
