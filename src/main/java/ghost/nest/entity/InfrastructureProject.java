package ghost.nest.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.LocalDate;

@Entity
@Table(name = "infrastructure_projects")
@Data
@NoArgsConstructor
public class InfrastructureProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point location;

    private LocalDate expectedCompletionDate;

    @Column(nullable = false)
    private Double impactScore;  // 0-1

    @Builder
    public InfrastructureProject(Long id, String name, Double latitude, Double longitude, LocalDate expectedCompletionDate, Double impactScore) {
        this.id = id;
        this.name = name;
        this.expectedCompletionDate = expectedCompletionDate;
        this.impactScore = impactScore;
        if (latitude != null && longitude != null) {
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
            this.location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        }
    }

    // Helper getters/setters for compatibility

    public Double getLatitude() {
        return location != null ? location.getY() : null;
    }

    public void setLatitude(Double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        double lng = this.location != null ? this.location.getX() : 0.0;
        this.location = geometryFactory.createPoint(new Coordinate(lng, latitude != null ? latitude : 0.0));
    }

    public Double getLongitude() {
        return location != null ? location.getX() : null;
    }

    public void setLongitude(Double longitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        double lat = this.location != null ? this.location.getY() : 0.0;
        this.location = geometryFactory.createPoint(new Coordinate(longitude != null ? longitude : 0.0, lat));
    }
}
