package ghost.nest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MlResponseDTO {
    // Shared ML interface response
    private Double livabilityScore;
    private Double predictedRoi;
    private String confidence;
    private Map<String, Double> breakdown;
    private String explanation;
}
