package confeti.confetibatchserver.domain.music.topartist.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import confeti.confetibatchserver.domain.music.topartist.TopArtist;
import confeti.confetibatchserver.domain.music.topartist.infra.repository.TopArtistRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TopArtistServiceTest {

    @Mock
    private TopArtistRepository topArtistRepository;

    @InjectMocks
    private TopArtistService topArtistService;

    @Test
    @DisplayName("refresh - 기존 데이터 삭제 후 새 데이터 저장")
    void refresh_DeleteAndSaveAll() {
        // given
        List<String> artistIds = List.of("artist1", "artist2", "artist3");

        // when
        topArtistService.refresh(artistIds);

        // then
        verify(topArtistRepository, times(1)).deleteAllInBatch();

        ArgumentCaptor<List<TopArtist>> captor = ArgumentCaptor.forClass(List.class);
        verify(topArtistRepository, times(1)).saveAll(captor.capture());

        List<TopArtist> savedTopArtists = captor.getValue();
        assertThat(savedTopArtists).hasSize(3);
        assertThat(savedTopArtists.get(0).getArtistId()).isEqualTo("artist1");
        assertThat(savedTopArtists.get(0).getRanking()).isEqualTo(1);
        assertThat(savedTopArtists.get(1).getArtistId()).isEqualTo("artist2");
        assertThat(savedTopArtists.get(1).getRanking()).isEqualTo(2);
        assertThat(savedTopArtists.get(2).getArtistId()).isEqualTo("artist3");
        assertThat(savedTopArtists.get(2).getRanking()).isEqualTo(3);
    }

    @Test
    @DisplayName("refresh - 빈 리스트 처리")
    void refresh_EmptyList() {
        // given
        List<String> artistIds = List.of();

        // when
        topArtistService.refresh(artistIds);

        // then
        verify(topArtistRepository, times(1)).deleteAllInBatch();

        ArgumentCaptor<List<TopArtist>> captor = ArgumentCaptor.forClass(List.class);
        verify(topArtistRepository, times(1)).saveAll(captor.capture());

        List<TopArtist> savedTopArtists = captor.getValue();
        assertThat(savedTopArtists).isEmpty();
    }
}
