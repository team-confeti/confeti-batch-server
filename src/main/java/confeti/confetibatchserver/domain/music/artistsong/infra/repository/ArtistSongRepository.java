package confeti.confetibatchserver.domain.music.artistsong.infra.repository;

import confeti.confetibatchserver.domain.music.artistsong.ArtistSong;
import confeti.confetibatchserver.domain.music.song.Song;
import feign.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ArtistSongRepository extends JpaRepository<ArtistSong, Long> {

    @Query(value = """
        SELECT s
        FROM Song as s
        JOIN ArtistSong AS a_s ON a_s.artistId = :artistId
        """)
    List<Song> findAllSongByArtistId(@Param("artistId") String artistId);
}
