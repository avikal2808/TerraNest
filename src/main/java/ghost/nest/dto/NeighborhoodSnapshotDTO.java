package ghost.nest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NeighborhoodSnapshotDTO {
    private Double latitude;
    private Double longitude;
    private Double radiusMeters;
    
    private Double livabilityScore;
    private Map<String, Double> livabilityBreakdown;
    
    private List<NearbyFeatureDTO> nearestInfrastructure;
    private List<NearbyFeatureDTO> nearestCrimes;
    
    private Integer totalPropertiesNearby;
    private Double averagePropertyPriceNearby;
    
    private String safetyRating; // e.g., "High", "Moderate", "Low"
}
