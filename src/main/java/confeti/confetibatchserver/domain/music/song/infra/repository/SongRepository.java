package confeti.confetibatchserver.domain.music.song.infra.repository;

import confeti.confetibatchserver.domain.music.song.Song;
import confeti.confetibatchserver.domain.music.song.projection.SongProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SongRepository extends JpaRepository<Song, String>, SongJdbcRepository {

    @Query("""
        SELECT 
                s.id as id,
                s.artistName as artistName,
                s.trackName as trackName,
                s.artworkUrl as artworkUrl,
                s.previewUrl as previewUrl
        FROM Song s
        JOIN ArtistSong a_s ON a_s.songId = s.id AND a_s.artistId = :artistId
        """)
    List<SongProjection> findAllSongByArtistId(@Param("artistId") String artistId);
}

