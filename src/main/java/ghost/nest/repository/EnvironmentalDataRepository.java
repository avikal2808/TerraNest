package ghost.nest.repository;

import ghost.nest.entity.EnvironmentalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnvironmentalDataRepository extends JpaRepository<EnvironmentalData, Long> {

    @Query(value = "SELECT * FROM environmental_data e WHERE ST_DWithin(e.location::geography, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, :radius)", nativeQuery = true)
    List<EnvironmentalData> findWithinRadius(@Param("lat") Double lat,
                                             @Param("lng") Double lng,
                                             @Param("radius") Double radiusMeters);
}
