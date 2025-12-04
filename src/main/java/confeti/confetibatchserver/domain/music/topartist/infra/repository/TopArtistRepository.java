package confeti.confetibatchserver.domain.music.topartist.infra.repository;

import confeti.confetibatchserver.domain.music.topartist.TopArtist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopArtistRepository extends JpaRepository<TopArtist, Long> {

}
