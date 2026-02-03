package confeti.confetibatchserver.domain.music.artist.infra.repository;

import confeti.confetibatchserver.domain.music.artist.Artist;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtistRepository extends JpaRepository<Artist, String>, ArtistJdbcRepository {

    @Query("SELECT a.id FROM Artist a WHERE a.id IN :ids")
    Set<String> findIdsIn(@Param("ids") Collection<String> ids);
}

