package confeti.confetibatchserver.domain.music.relatedartist.infra.repository;

import confeti.confetibatchserver.domain.music.relatedartist.vo.ConfetiRelatedArtist;
import java.util.Collection;
import java.util.List;

public interface RelatedArtistJdbcRepository {

    List<ConfetiRelatedArtist> findAllConfetiRelatedArtistIdsByArtistIds(
        Collection<String> artistIds);

    void bulkInsert(Collection<ConfetiRelatedArtist> relatedArtists);

    void bulkDelete(Collection<ConfetiRelatedArtist> relatedArtists);
}
