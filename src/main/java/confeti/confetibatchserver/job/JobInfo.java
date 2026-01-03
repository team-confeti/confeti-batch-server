package confeti.confetibatchserver.job;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobInfo {

    ARTIST_SONG_SYNC_JOB("artistSongSyncJob"),
    RELATED_ARTIST_SYNC_JOB("relatedArtistSyncJob"),
    TOP_ARTIST_SYNC_JOB("topArtistSyncJob");

    private final String jobName;
}
