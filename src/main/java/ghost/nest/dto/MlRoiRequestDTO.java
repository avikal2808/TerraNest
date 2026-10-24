package ghost.nest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MlRoiRequestDTO {
    private Double price;
    private Double areaSqft;
    private Double infrastructureImpact;
    private Integer upcomingProjectsCount;
}
