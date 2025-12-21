package confeti.confetibatchserver.domain.music.artistsong.infra.repository;

import confeti.confetibatchserver.domain.music.artistsong.ArtistSong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistSongRepository extends JpaRepository<ArtistSong, Long> {

}
