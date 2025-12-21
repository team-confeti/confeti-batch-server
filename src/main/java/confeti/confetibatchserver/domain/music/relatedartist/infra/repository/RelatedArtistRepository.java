package confeti.confetibatchserver.domain.music.relatedartist.infra.repository;

import confeti.confetibatchserver.domain.music.relatedartist.RelatedArtist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelatedArtistRepository extends JpaRepository<RelatedArtist, Long> {

}
