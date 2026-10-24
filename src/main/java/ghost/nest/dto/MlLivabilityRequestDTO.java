package ghost.nest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MlLivabilityRequestDTO {
    private Double airQuality;
    private Double waterReliability;
    private Double safety;
    private Double infrastructure;
}
