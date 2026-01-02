package confeti.confetibatchserver.job;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobInfo {

    ARTIST_SONG_SYNC_JOB("artistSongSyncJob"),
    TOP_ARTIST_SYNC("topArtistSync");

    private final String jobName;
}
