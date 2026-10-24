// LivabilityResponseDTO.java
package ghost.nest.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class LivabilityResponseDTO {
    private Long propertyId;
    private Double overallScore;        // 0-100
    private Map<String, Double> breakdown; // e.g., "airQuality": 85, "safety": 70, ...
    private String explanation;
}
