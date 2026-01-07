package confeti.confetibatchserver.job;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StepInfo {
    ARTIST_SYNC_STEP(JobInfo.ARTIST_SONG_SYNC_JOB, "artistSyncStep"),
    ARTIST_SONG_SYNC_STEP(JobInfo.ARTIST_SONG_SYNC_JOB, "artistSongSyncStep"),

    RELATED_ARTIST_SYNC_STEP(JobInfo.RELATED_ARTIST_SYNC_JOB, "relatedArtistSyncStep");

    private final JobInfo jobInfo;
    private final String name;
}
