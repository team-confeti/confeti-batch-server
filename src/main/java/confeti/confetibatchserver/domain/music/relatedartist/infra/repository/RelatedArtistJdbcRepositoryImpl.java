package confeti.confetibatchserver.domain.music.relatedartist.infra.repository;

import confeti.confetibatchserver.domain.music.relatedartist.vo.ConfetiRelatedArtist;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RelatedArtistJdbcRepositoryImpl implements RelatedArtistJdbcRepository {

    private static final String SELECT_RELATED_ARTIST_QUERY = """
        SELECT
            ra.id as id,
            ra.artist_id as artistId,
            ra.related_artist_id as relatedArtistId
        FROM related_artists as ra
        WHERE ra.artist_id IN (:artistId)
        """;

    private static final String BULK_INSERT_RELATED_ARTIST_QUERY = """
        INSERT IGNORE INTO related_artists (artist_id, related_artist_id, created_at, updated_at)
        VALUES (:artistId, :relatedArtistId, NOW(), null)
        """;

    private static final String BULK_DELETE_RELATED_ARTIST_QUERY = """
        DELETE
        FROM related_artists as ra
        WHERE ra.artist_id = :artistId AND ra.related_artist_id = :related_artist_id
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

    @Override
    public void bulkInsert(Collection<ConfetiRelatedArtist> relatedArtists) {
        MapSqlParameterSource[] parameterSource = relatedArtists.stream()
            .map(relatedArtist -> new MapSqlParameterSource()
                .addValue("artistId", relatedArtist.getArtistId())
                .addValue("relatedArtistId", relatedArtist.getRelatedArtistId())
            ).toArray(MapSqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(BULK_INSERT_RELATED_ARTIST_QUERY, parameterSource);
    }

    @Override
    public void bulkDelete(Collection<ConfetiRelatedArtist> relatedArtists) {
        MapSqlParameterSource[] parameterSource = relatedArtists.stream()
            .map(relatedArtist -> new MapSqlParameterSource()
                .addValue("artistId", relatedArtist.getArtistId())
                .addValue("relatedArtistId", relatedArtist.getRelatedArtistId())
            ).toArray(MapSqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(BULK_DELETE_RELATED_ARTIST_QUERY, parameterSource);
    }

}
