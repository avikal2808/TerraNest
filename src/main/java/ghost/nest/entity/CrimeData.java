package ghost.nest.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.LocalDateTime;

@Entity
@Table(name = "crime_data")
@Data
@NoArgsConstructor
public class CrimeData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point location;

    @Column(nullable = false)
    private String crimeType;

    @Column(nullable = false)
    private Integer severity;  // 1-10 scale

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Builder
    public CrimeData(Long id, Double latitude, Double longitude, String crimeType, Integer severity, LocalDateTime timestamp) {
        this.id = id;
        this.crimeType = crimeType;
        this.severity = severity;
        this.timestamp = timestamp;
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
