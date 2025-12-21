package confeti.confetibatchserver.job;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StepInfo {
    ARTIST_SYNC_STEP(JobInfo.ARTIST_SYNC_JOB, "artistSyncStep");

    private final JobInfo jobInfo;
    private final String name;
}
