package confeti.confetibatchserver.scheduler;

import static confeti.confetibatchserver.job.JobInfo.TOP_ARTIST_SYNC_JOB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import confeti.confetibatchserver.domain.batch.jobconfig.JobConfig;
import confeti.confetibatchserver.domain.batch.jobconfig.application.JobConfigService;
import confeti.confetibatchserver.domain.music.artist.application.ArtistService;
import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import confeti.confetibatchserver.domain.music.topartist.application.TopArtistService;
import confeti.confetibatchserver.external.service.MusicAPIHandler;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TopArtistSyncScheduleTest {

    @Mock
    private MusicAPIHandler musicAPIHandler;

    @Mock
    private TopArtistService topArtistService;

    @Mock
    private JobConfigService jobConfigService;

    @Mock
    private JobConfig jobConfig;

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private TopArtistSyncSchedule topArtistSyncSchedule;

    @Test
    @DisplayName("TopArtist 동기화 - 정상 동작")
    void runTopArtistSync_Success() {
        // given
        given(jobConfigService.getByJobInfo(TOP_ARTIST_SYNC_JOB)).willReturn(jobConfig);
        given(jobConfig.isActive()).willReturn(true);

        ConfetiArtist artist1 = createMockArtist("artist1");
        ConfetiArtist artist2 = createMockArtist("artist2");
        ConfetiArtist artist3 = createMockArtist("artist3");

        given(topArtistService.getTopArtists(anyInt()))
            .willReturn(List.of(artist1, artist2, artist3));

        // when
        topArtistSyncSchedule.runTopArtistSync();

        // then
        ArgumentCaptor<List<ConfetiArtist>> upsertArtistCaptor = ArgumentCaptor.forClass(
            List.class);
        verify(artistService, times(1)).upsertArtists(upsertArtistCaptor.capture());

        ArgumentCaptor<List<String>> refreshCaptor = ArgumentCaptor.forClass(List.class);
        verify(topArtistService, times(1)).refresh(refreshCaptor.capture());

        List<ConfetiArtist> upsertArtist = upsertArtistCaptor.getValue();
        assertThat(upsertArtist).isEqualTo(List.of(artist1, artist2, artist3));

        List<String> refreshCapturedIds = refreshCaptor.getValue();
        assertThat(refreshCapturedIds).containsExactly("artist1", "artist2", "artist3");
    }

    @Test
    @DisplayName("TopArtist 동기화 - 비활성화 시 실행 안 함")
    void runTopArtistSync_Disabled() {
        // given
        given(jobConfigService.getByJobInfo(TOP_ARTIST_SYNC_JOB)).willReturn(jobConfig);
        given(jobConfig.isActive()).willReturn(false);

        // when
        topArtistSyncSchedule.runTopArtistSync();

        // then
        verify(musicAPIHandler, never()).getTopSongs(anyInt());
        verify(topArtistService, never()).refresh(List.of());
    }

    @Test
    @DisplayName("TopArtist 동기화 - 빈 응답 처리")
    void runTopArtistSync_EmptyResponse() {
        // given
        given(jobConfigService.getByJobInfo(TOP_ARTIST_SYNC_JOB)).willReturn(jobConfig);
        given(jobConfig.isActive()).willReturn(true);
        given(topArtistService.getTopArtists(anyInt()))
            .willReturn(List.of());

        // when
        topArtistSyncSchedule.runTopArtistSync();

        // then
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<ConfetiArtist>> upsertCaptor = ArgumentCaptor.forClass(List.class);
        verify(topArtistService, times(1)).refresh(captor.capture());
        verify(artistService, times(1)).upsertArtists(upsertCaptor.capture());

        List<String> artistIds = captor.getValue();
        assertThat(artistIds).isEmpty();

        List<ConfetiArtist> artists = upsertCaptor.getValue();
        assertThat(artists).isEmpty();
    }

    private ConfetiArtist createMockArtist(String id) {
        return ConfetiArtist.of(id, "name-" + id, "artworkUrl-" + id);
    }
}
