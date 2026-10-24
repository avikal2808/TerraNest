// RoiResponseDTO.java
package ghost.nest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoiResponseDTO {
    private Long propertyId;
    private Double predictedRoiPercentage;
    private String confidenceLevel;  // LOW, MEDIUM, HIGH
}
