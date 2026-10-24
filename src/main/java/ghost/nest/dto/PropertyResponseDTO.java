// PropertyResponseDTO.java
package ghost.nest.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PropertyResponseDTO {
    private Long id;
    private String title;
    private BigDecimal price;
    private Double latitude;
    private Double longitude;
    private Double areaSqft;
}
