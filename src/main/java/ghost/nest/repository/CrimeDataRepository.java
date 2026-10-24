package ghost.nest.repository;

import ghost.nest.entity.CrimeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrimeDataRepository extends JpaRepository<CrimeData, Long> {

    @Query(value = "SELECT * FROM crime_data c WHERE ST_DWithin(c.location::geography, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, :radius) AND c.timestamp >= :since", nativeQuery = true)
    List<CrimeData> findRecentWithinRadius(@Param("lat") Double lat,
                                           @Param("lng") Double lng,
                                           @Param("radius") Double radiusMeters,
                                           @Param("since") java.time.LocalDateTime since);
}
