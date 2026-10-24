// PropertyRequestDTO.java
package ghost.nest.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertyRequestDTO {
    @NotBlank
    private String title;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double latitude;

    @NotNull
    @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double longitude;

    @NotNull
    @Positive
    private Double areaSqft;
}
