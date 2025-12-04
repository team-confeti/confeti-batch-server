package confeti.confetibatchserver.domain.music.artist.infra.repository;

import confeti.confetibatchserver.domain.music.artist.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, String> {

}

