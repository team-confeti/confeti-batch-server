package confeti.confetibatchserver.domain.music.topartist.application;


import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import confeti.confetibatchserver.domain.music.song.dto.ConfetiSongWithArtistIds;
import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import confeti.confetibatchserver.domain.music.topartist.TopArtist;
import confeti.confetibatchserver.domain.music.topartist.infra.repository.TopArtistRepository;
import confeti.confetibatchserver.external.service.MusicAPIHandler;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopArtistService {

    private final TopArtistRepository topArtistRepository;
    private final MusicAPIHandler musicAPIHandler;

    public List<ConfetiArtist> getTopArtists(int limit) {
        List<ConfetiSong> topSongs = musicAPIHandler.getTopSongs(limit);
        List<String> songIds = topSongs.stream()
            .map(ConfetiSong::getId)
            .toList();

        List<ConfetiSongWithArtistIds> songsWithArtists = musicAPIHandler.getSongWithArtistIdsByIds(
            songIds);
        List<String> artistIds = songsWithArtists.stream()
            .map(ConfetiSongWithArtistIds::artistIds)
            .flatMap(Collection::stream)
            .distinct()
            .toList();
        return musicAPIHandler.getArtistsByIds(artistIds);
    }

    @Transactional
    public void refresh(List<String> artistIds) {
        topArtistRepository.deleteAllInBatch();

        List<TopArtist> topArtists = IntStream.range(0, artistIds.size())
            .mapToObj(idx -> TopArtist.create(artistIds.get(idx), idx + 1))
            .toList();

        topArtistRepository.saveAll(topArtists);
    }
}
