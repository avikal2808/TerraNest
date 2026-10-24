package ghost.nest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyFeatureDTO {
    private String name;
    private String type;
    private Double distanceMeters;
    private Double latitude;
    private Double longitude;
    private Object details; // Can hold severity for crime, or impact for infra
}
