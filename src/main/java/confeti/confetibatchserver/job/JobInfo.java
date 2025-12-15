package confeti.confetibatchserver.job;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobInfo {

    ARTIST_SYNC_JOB("artistSyncJob");

    private final String jobName;
}
