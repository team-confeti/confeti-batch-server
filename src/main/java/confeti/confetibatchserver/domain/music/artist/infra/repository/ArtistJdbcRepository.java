package confeti.confetibatchserver.domain.music.artist.infra.repository;

import confeti.confetibatchserver.domain.music.artist.Artist;
import java.util.Collection;

public interface ArtistJdbcRepository {

    void upsertArtists(Collection<Artist> artists);
}
