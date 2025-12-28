package confeti.confetibatchserver.domain.music.song.infra.repository;

import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import java.util.List;

public interface SongJdbcRepository {

    void upsertSongsWithArtistId(String artistId, List<ConfetiSong> songs);

    List<ConfetiSong> findAllConfetiSongsByArtistId(String artistId);
}
