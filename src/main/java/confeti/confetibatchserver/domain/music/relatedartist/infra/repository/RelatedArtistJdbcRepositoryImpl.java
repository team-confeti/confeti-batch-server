package confeti.confetibatchserver.domain.music.relatedartist.infra.repository;

import confeti.confetibatchserver.domain.music.relatedartist.vo.ConfetiRelatedArtist;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RelatedArtistJdbcRepositoryImpl implements RelatedArtistJdbcRepository {

    private static final String SELECT_RELATED_ARTIST_QUERY = """
        SELECT
            ra.id as id,
            ra.artist_id as artistId
            ra.related_artist_id as relatedArtistId
        FROM related_artists as ra
        WHERE ra.artist_id IN (:artistId)
        """;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<ConfetiRelatedArtist> findAllConfetiRelatedArtistIdsByArtistIds(
        Collection<String> artistIds
    ) {
        Map<String, Object> params = Map.of("artistId", artistIds);

        return namedParameterJdbcTemplate.query(
            SELECT_RELATED_ARTIST_QUERY,
            params,
            (rs, rowNum) -> ConfetiRelatedArtist.builder()
                .id(rs.getLong("id"))
                .artistId(rs.getString("artist_id"))
                .relatedArtistId(rs.getString("related_artist_id"))
                .build());
    }

}
