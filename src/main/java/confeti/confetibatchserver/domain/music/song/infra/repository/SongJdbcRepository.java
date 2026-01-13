package confeti.confetibatchserver.domain.music.song.infra.repository;

import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import confeti.confetibatchserver.job.artistsongsync.dto.ArtistIdWithSongs;
import java.util.List;

public interface SongJdbcRepository {

    void upsertSongsWithArtistId(List<ArtistIdWithSongs> artistIdWithSongs);

    List<ConfetiSong> findAllConfetiSongsByArtistId(String artistId);
}
