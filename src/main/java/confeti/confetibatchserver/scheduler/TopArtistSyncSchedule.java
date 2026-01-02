package confeti.confetibatchserver.scheduler;

import static confeti.confetibatchserver.job.JobInfo.TOP_ARTIST_SYNC;

import confeti.confetibatchserver.domain.batch.jobconfig.JobConfig;
import confeti.confetibatchserver.domain.batch.jobconfig.application.JobConfigService;
import confeti.confetibatchserver.domain.music.topartist.application.TopArtistService;
import confeti.confetibatchserver.external.client.dto.music.AppleMusicMusicResponse;
import confeti.confetibatchserver.external.service.MusicAPIHandler;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TopArtistSyncSchedule {

    private static final int TOP_SONGS_FETCH_SIZE = 200;

    private final MusicAPIHandler musicAPIHandler;
    private final TopArtistService topArtistService;
    private final JobConfigService jobConfigService;

    @Scheduled(
        cron = "${schedules.top-artist-sync.cron}",
        zone = "${schedules.top-artist-sync.zone}"
    )
    public void runTopArtistSync() {
        JobConfig jobConfig = jobConfigService.getByJobInfo(TOP_ARTIST_SYNC);
        if (!jobConfig.isActive()) {
            log.info("TopArtist sync is disabled");
            return;
        }

        log.info("TopArtist sync started");

        try {
            List<AppleMusicMusicResponse> topSongs = musicAPIHandler.getTopSongs(TOP_SONGS_FETCH_SIZE);

            Set<String> songIds = topSongs.stream()
                .map(AppleMusicMusicResponse::id)
                .collect(Collectors.toCollection(LinkedHashSet::new));

            List<AppleMusicMusicResponse> songsWithArtists = musicAPIHandler.getSongsByIds(songIds);

            List<String> artistIds = songsWithArtists.stream()
                .flatMap(song -> song.getArtistIds().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .stream()
                .toList();

            topArtistService.refresh(artistIds);

            log.info("TopArtist sync completed. Total artists: {}", artistIds.size());
        } catch (Exception e) {
            log.error("TopArtist sync failed", e);
        }
    }
}
