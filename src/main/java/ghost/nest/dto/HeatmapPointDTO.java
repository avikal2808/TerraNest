package ghost.nest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapPointDTO {
    private Double latitude;
    private Double longitude;
    private Double intensity; // e.g., count of properties or average price
    private Double weight;    // secondary metric
}
