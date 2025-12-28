package confeti.confetibatchserver.domain.music.song.application;

import confeti.confetibatchserver.domain.music.song.infra.repository.SongRepository;
import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;

    @Transactional
    public void upsert(String artistId, List<ConfetiSong> songs) {
        Map<String, ConfetiSong> songMapByArtistId = getConfetiSongMapByArtistId(
            artistId);
        List<ConfetiSong> upsertSongs = getUpsertSongs(songs, songMapByArtistId);

        songRepository.upsertSongsWithArtistId(artistId, upsertSongs);
    }

    private Map<String, ConfetiSong> getConfetiSongMapByArtistId(String artistId) {
        return songRepository.findAllConfetiSongsByArtistId(artistId).stream()
            .collect(Collectors.toMap(ConfetiSong::getId, Function.identity()));
    }

    private List<ConfetiSong> getUpsertSongs(
        List<ConfetiSong> fetchedSongs,
        Map<String, ConfetiSong> songMapByArtistId
    ) {
        List<ConfetiSong> upsertSongs = new ArrayList<>();
        for (ConfetiSong fetchedSong : fetchedSongs) {
            ConfetiSong song = songMapByArtistId.get(fetchedSong.getId());
            if (song == null) {
                upsertSongs.add(fetchedSong);
                continue;
            }
            if (fetchedSong.isDifferentData(song)) {
                upsertSongs.add(fetchedSong);
            }
        }
        return upsertSongs;
    }
}
