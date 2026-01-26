package confeti.confetibatchserver.scheduler;

import static confeti.confetibatchserver.job.JobInfo.TOP_ARTIST_SYNC_JOB;

import confeti.confetibatchserver.domain.batch.jobconfig.JobConfig;
import confeti.confetibatchserver.domain.batch.jobconfig.application.JobConfigService;
import confeti.confetibatchserver.domain.music.artist.application.ArtistService;
import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import confeti.confetibatchserver.domain.music.topartist.application.TopArtistService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TopArtistSyncSchedule {

    private static final int TOP_SONGS_FETCH_SIZE = 200;

    private final TopArtistService topArtistService;
    private final JobConfigService jobConfigService;
    private final ArtistService artistService;

    @Scheduled(
        cron = "${schedules.top-artist-sync.cron}",
        zone = "${schedules.top-artist-sync.zone}"
    )
    public void runTopArtistSync() {
        JobConfig jobConfig = jobConfigService.getByJobInfo(TOP_ARTIST_SYNC_JOB);
        if (!jobConfig.isActive()) {
            log.info("TopArtist sync is disabled");
            return;
        }

        log.info("TopArtist sync started");

        try {
            List<ConfetiArtist> topArtists = topArtistService.getTopArtists(TOP_SONGS_FETCH_SIZE);
            artistService.upsertArtists(topArtists);
            List<String> topArtistIds = topArtists.stream().map(ConfetiArtist::getId).toList();
            topArtistService.refresh(topArtistIds);

            log.info("TopArtist sync completed. Total artists: {}", topArtists.size());
        } catch (Exception e) {
            log.error("TopArtist sync failed", e);
        }
    }
}
