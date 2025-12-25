package confeti.confetibatchserver.domain.music.song.application;

import confeti.confetibatchserver.domain.music.artistsong.infra.repository.ArtistSongRepository;
import confeti.confetibatchserver.domain.music.song.infra.repository.SongRepository;
import confeti.confetibatchserver.domain.music.song.projection.SongProjection;
import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final ArtistSongRepository artistSongRepository;

    public Map<String, SongProjection> getConfetiSongMapByArtistId(String artistId) {
        return songRepository.findAllSongByArtistId(artistId).stream()
            .collect(Collectors.toMap(SongProjection::getId, Function.identity()));
    }

    public void upsertSongsWithArtistId(String artistId, List<ConfetiSong> songs) {
        songRepository.upsertSongsWithArtistId(artistId, songs);
    }
}
