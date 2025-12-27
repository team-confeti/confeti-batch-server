package confeti.confetibatchserver.domain.music.song.infra.repository;

import confeti.confetibatchserver.domain.music.song.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, String>, SongJdbcRepository {

}

