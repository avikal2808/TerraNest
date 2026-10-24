package ghost.nest.controller;

import ghost.nest.dto.NeighborhoodSnapshotDTO;
import ghost.nest.service.NeighborhoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/neighborhood")
@RequiredArgsConstructor
public class NeighborhoodController {

    private final NeighborhoodService neighborhoodService;

    @GetMapping("/snapshot")
    public ResponseEntity<NeighborhoodSnapshotDTO> getSnapshot(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) Double radius) {
        return ResponseEntity.ok(neighborhoodService.getSnapshot(lat, lng, radius));
    }
}
