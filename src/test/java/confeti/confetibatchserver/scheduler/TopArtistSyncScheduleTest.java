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
import confeti.confetibatchserver.domain.music.topartist.application.TopArtistService;
import confeti.confetibatchserver.external.client.dto.music.AppleMusicMusicArtistResponse;
import confeti.confetibatchserver.external.client.dto.music.AppleMusicMusicArtistsResponse;
import confeti.confetibatchserver.external.client.dto.music.AppleMusicMusicRelationshipsResponse;
import confeti.confetibatchserver.external.client.dto.music.AppleMusicMusicResponse;
import confeti.confetibatchserver.external.service.MusicAPIHandler;
import java.util.List;
import java.util.Set;
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

    @InjectMocks
    private TopArtistSyncSchedule topArtistSyncSchedule;

    @Test
    @DisplayName("TopArtist 동기화 - 정상 동작")
    void runTopArtistSync_Success() {
        // given
        AppleMusicMusicResponse song1 = createMusicResponse("song1", List.of("artist1", "artist2"));
        AppleMusicMusicResponse song2 = createMusicResponse("song2", List.of("artist2", "artist3"));

        given(jobConfigService.getByJobInfo(TOP_ARTIST_SYNC_JOB)).willReturn(jobConfig);
        given(jobConfig.isActive()).willReturn(true);
        given(musicAPIHandler.getTopSongs(anyInt()))
            .willReturn(List.of(song1, song2));
        given(musicAPIHandler.getSongsByIds(Set.of("song1", "song2")))
            .willReturn(List.of(song1, song2));

        // when
        topArtistSyncSchedule.runTopArtistSync();

        // then
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(topArtistService, times(1)).refresh(captor.capture());

        List<String> artistIds = captor.getValue();
        assertThat(artistIds).containsExactly("artist1", "artist2", "artist3");
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
        given(musicAPIHandler.getTopSongs(anyInt()))
            .willReturn(List.of());
        given(musicAPIHandler.getSongsByIds(Set.of()))
            .willReturn(List.of());

        // when
        topArtistSyncSchedule.runTopArtistSync();

        // then
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(topArtistService, times(1)).refresh(captor.capture());

        List<String> artistIds = captor.getValue();
        assertThat(artistIds).isEmpty();
    }

    private AppleMusicMusicResponse createMusicResponse(String songId, List<String> artistIds) {
        List<AppleMusicMusicArtistResponse> artists = artistIds.stream()
            .map(AppleMusicMusicArtistResponse::new)
            .toList();

        AppleMusicMusicArtistsResponse artistsResponse = new AppleMusicMusicArtistsResponse(artists);
        AppleMusicMusicRelationshipsResponse relationships = new AppleMusicMusicRelationshipsResponse(artistsResponse);

        return new AppleMusicMusicResponse(songId, "songs", null, relationships);
    }
}
