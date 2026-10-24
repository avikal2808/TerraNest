// PropertyResponseDTO.java
package ghost.nest.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class PropertyResponseDTO {
    private Long id;
    private String title;
    private BigDecimal price;
    private Double latitude;
    private Double longitude;
    private Double areaSqft;

    public Map<String, Object> toGeoJson() {
        return Map.of(
            "type", "Feature",
            "geometry", Map.of(
                "type", "Point",
                "coordinates", new Double[]{longitude, latitude}
            ),
            "properties", Map.of(
                "id", id,
                "title", title,
                "price", price,
                "areaSqft", areaSqft
            )
        );
    }
}
