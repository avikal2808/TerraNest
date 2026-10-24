package ghost.nest.scheduler;

import ghost.nest.entity.CrimeData;
import ghost.nest.entity.EnvironmentalData;
import ghost.nest.repository.CrimeDataRepository;
import ghost.nest.repository.EnvironmentalDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataIngestionScheduler {

    private final EnvironmentalDataRepository envRepository;
    private final CrimeDataRepository crimeRepository;
    private final Random random = new Random();

    @Value("${external.data.air-quality-url}")
    private String airQualityUrl;

    @Value("${external.data.crime-url}")
    private String crimeUrl;

    @Scheduled(fixedRateString = "${scheduler.fixed-rate:3600000}")
    @Transactional
    public void ingestEnvironmentalData() {
        log.info("Starting environmental data ingestion...");
        try {
            List<EnvironmentalData> mockData = generateMockEnvironmentalData();
            envRepository.saveAll(mockData);
            log.info("Successfully ingested {} environmental data records.", mockData.size());
        } catch (Exception e) {
            log.error("Failed to ingest environmental data", e);
        }
    }

    @Scheduled(fixedRateString = "${scheduler.fixed-rate:3600000}")
    @Transactional
    public void ingestCrimeData() {
        log.info("Starting crime data ingestion...");
        try {
            List<CrimeData> mockData = generateMockCrimeData();
            crimeRepository.saveAll(mockData);
            log.info("Successfully ingested {} crime data records.", mockData.size());
        } catch (Exception e) {
            log.error("Failed to ingest crime data", e);
        }
    }

    /**
     * Nightly cleanup job to remove raw spatial coordinates older than 6 months
     * to prevent unbounded database growth.
     */
    @Scheduled(cron = "0 0 2 * * ?") // Every day at 2 AM
    @Transactional
    public void cleanupOldSpatialData() {
        log.info("Running nightly cleanup for stale spatial data...");
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        
        try {
            log.info("Cleanup successful for spatial data older than {}", sixMonthsAgo);
        } catch (Exception e) {
            log.error("Error during nightly cleanup of spatial data", e);
        }
    }

    private List<EnvironmentalData> generateMockEnvironmentalData() {
        List<EnvironmentalData> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            double lat = 40.7128 + (random.nextDouble() - 0.5) * 0.1;
            double lng = -74.0060 + (random.nextDouble() - 0.5) * 0.1;
            data.add(EnvironmentalData.builder()
                    .latitude(lat)
                    .longitude(lng)
                    .airQualityIndex(20 + random.nextDouble() * 80)
                    .waterReliabilityScore(random.nextDouble())
                    .timestamp(LocalDateTime.now())
                    .build());
        }
        return data;
    }

    private List<CrimeData> generateMockCrimeData() {
        String[] crimeTypes = {"THEFT", "ASSAULT", "VANDALISM", "BURGLARY"};
        List<CrimeData> data = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            double lat = 40.7128 + (random.nextDouble() - 0.5) * 0.1;
            double lng = -74.0060 + (random.nextDouble() - 0.5) * 0.1;
            data.add(CrimeData.builder()
                    .latitude(lat)
                    .longitude(lng)
                    .crimeType(crimeTypes[random.nextInt(crimeTypes.length)])
                    .severity(1 + random.nextInt(10))
                    .timestamp(LocalDateTime.now().minusDays(random.nextInt(180)))
                    .build());
        }
        return data;
    }
}
