package confeti.confetibatchserver.domain.music.song.infra.repository;

import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SongJdbcRepositoryImpl implements SongJdbcRepository {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    private final String BULK_UPSERT_SONGS_SQL = """
            INSERT INTO songs (id, track_name, artwork_url, artist_name, preview_url, created_at, updated_at)
            VALUES (:id, :trackName, :artworkUrl, :artistName, :previewUrl, NOW(), null)
            ON DUPLICATE KEY UPDATE
                track_name = VALUES(track_name),
                artwork_url = VALUES(artwork_url),
                artist_name = VALUES(artist_name),
                preview_url = VALUES(preview_url),
                updated_at = NOW()
        """;
    private final String BULK_INSERT_ARTIST_SONGS_SQL = """
            INSERT IGNORE INTO artist_songs (song_id, artist_id)
            VALUES (:songId, :artistId)
        """;
    private final String SELECT_CONFETI_SONGS_BY_ARTIST_ID_SQL = """
            SELECT s.id as id,
               s.track_name as trackName,
               s.artwork_url as artworkUrl,
               s.artist_name as artistName,
               s.preview_url as previewUrl
            FROM songs as s
            INNER JOIN artist_songs as a_s ON a_s.song_id = s.id AND a_s.artist_id = :artistId
        """;

    @Transactional
    public void upsertSongsWithArtistId(String artistId, List<ConfetiSong> songs) {
        SqlParameterSource[] songParams = SqlParameterSourceUtils.createBatch(songs);
        namedJdbcTemplate.batchUpdate(BULK_UPSERT_SONGS_SQL, songParams);

        MapSqlParameterSource[] artistSongParams = songs.stream()
            .map(song -> new MapSqlParameterSource()
                .addValue("songId", song.getId())
                .addValue("artistId", artistId))
            .toArray(MapSqlParameterSource[]::new);
        namedJdbcTemplate.batchUpdate(BULK_INSERT_ARTIST_SONGS_SQL, artistSongParams);
    }

    @Override
    public List<ConfetiSong> findAllConfetiSongsByArtistId(String artistId) {
        Map<String, Object> params = Map.of("artistId", artistId);

        return namedJdbcTemplate.query(
            SELECT_CONFETI_SONGS_BY_ARTIST_ID_SQL,
            params,
            (rs, rowNum) -> ConfetiSong.builder()
                .id(rs.getString("id"))
                .trackName(rs.getString("trackName"))
                .artworkUrl(rs.getString("artworkUrl"))
                .artistName(rs.getString("artistName"))
                .previewUrl(rs.getString("previewUrl"))
                .build()
        );
    }
}
