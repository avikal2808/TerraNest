package ghost.nest.repository;

import ghost.nest.entity.InfrastructureProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfrastructureProjectRepository extends JpaRepository<InfrastructureProject, Long> {

    @Query(value = "SELECT * FROM infrastructure_projects i WHERE ST_DWithin(i.location::geography, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, :radius)", nativeQuery = true)
    List<InfrastructureProject> findWithinRadius(@Param("lat") Double lat,
                                                 @Param("lng") Double lng,
                                                 @Param("radius") Double radiusMeters);

    @Query(value = "SELECT *, ST_Distance(location::geography, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography) as distance " +
                   "FROM infrastructure_projects " +
                   "ORDER BY location <-> ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) " +
                   "LIMIT :limit", nativeQuery = true)
    List<Object[]> findNearestWithDistance(@Param("lat") Double lat, @Param("lng") Double lng, @Param("limit") int limit);
}
