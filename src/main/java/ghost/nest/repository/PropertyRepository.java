package ghost.nest.repository;

import ghost.nest.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query(value = "SELECT * FROM properties p WHERE ST_DWithin(p.location::geography, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, :radius)", nativeQuery = true)
    List<Property> findPropertiesWithinRadius(@Param("lat") Double lat,
                                              @Param("lng") Double lng,
                                              @Param("radius") Double radiusMeters);

    @Query(value = "SELECT ST_X(ST_SnapToGrid(location, :gridSize)) as lng, " +
                   "ST_Y(ST_SnapToGrid(location, :gridSize)) as lat, " +
                   "COUNT(*) as count, " +
                   "AVG(price) as avg_price " +
                   "FROM properties " +
                   "GROUP BY ST_SnapToGrid(location, :gridSize)", nativeQuery = true)
    List<Object[]> aggregatePropertiesByGrid(@Param("gridSize") Double gridSize);
}
