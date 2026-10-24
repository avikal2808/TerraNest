package ghost.nest.service;

import ghost.nest.dto.NearbyFeatureDTO;
import ghost.nest.dto.NeighborhoodSnapshotDTO;
import ghost.nest.dto.PropertyResponseDTO;
import ghost.nest.entity.Property;
import ghost.nest.repository.CrimeDataRepository;
import ghost.nest.repository.InfrastructureProjectRepository;
import ghost.nest.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NeighborhoodService {

    private final PropertyRepository propertyRepository;
    private final CrimeDataRepository crimeRepository;
    private final InfrastructureProjectRepository infraRepository;
    private final LivabilityService livabilityService;

    @Value("${geospatial.default-radius-meters:1000}")
    private Double defaultRadiusMeters;

    public NeighborhoodSnapshotDTO getSnapshot(Double lat, Double lng, Double radius) {
        Double searchRadius = radius != null ? radius : defaultRadiusMeters;
        log.info("Generating neighborhood snapshot for lat: {}, lng: {}, radius: {}", lat, lng, searchRadius);

        // 1. Get nearby properties for stats
        List<Property> properties = propertyRepository.findPropertiesWithinRadius(lat, lng, searchRadius);
        int totalProperties = properties.size();
        double avgPrice = properties.stream()
                .map(Property::getPrice)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0);

        // 2. Get nearest infrastructure (top 5)
        List<Object[]> nearestInfra = infraRepository.findNearestWithDistance(lat, lng, 5);
        List<NearbyFeatureDTO> infraFeatures = nearestInfra.stream()
                .map(row -> NearbyFeatureDTO.builder()
                        .name((String) row[1]) // name
                        .type("INFRASTRUCTURE")
                        .distanceMeters((Double) row[6]) // distance from ST_Distance
                        .latitude(null) // native query result mapping for geometry is complex, skipping for now
                        .longitude(null)
                        .details(row[5]) // impact_score
                        .build())
                .collect(Collectors.toList());

        // 3. Get nearest recent crimes (top 5)
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        List<Object[]> nearestCrimes = crimeRepository.findNearestRecentWithDistance(lat, lng, sixMonthsAgo, 5);
        List<NearbyFeatureDTO> crimeFeatures = nearestCrimes.stream()
                .map(row -> NearbyFeatureDTO.builder()
                        .name((String) row[2]) // crime_type
                        .type("CRIME")
                        .distanceMeters((Double) row[6]) // distance
                        .details(row[3]) // severity
                        .build())
                .collect(Collectors.toList());

        // 4. Calculate safety rating
        double avgSeverity = crimeFeatures.stream()
                .mapToDouble(f -> (Integer) f.getDetails())
                .average()
                .orElse(0.0);
        String safetyRating = avgSeverity < 3 ? "High" : (avgSeverity < 7 ? "Moderate" : "Low");

        // 5. Build DTO
        // Note: LivabilityService needs a Property ID, so we might need a coordinate-based livability method later.
        // For now, we return the snapshot without a coordinate-based ML score or we could mock/heuristic it.
        
        return NeighborhoodSnapshotDTO.builder()
                .latitude(lat)
                .longitude(lng)
                .radiusMeters(searchRadius)
                .nearestInfrastructure(infraFeatures)
                .nearestCrimes(crimeFeatures)
                .totalPropertiesNearby(totalProperties)
                .averagePropertyPriceNearby(avgPrice)
                .safetyRating(safetyRating)
                .build();
    }
}
