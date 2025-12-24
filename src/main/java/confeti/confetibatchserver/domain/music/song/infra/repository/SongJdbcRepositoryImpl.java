package confeti.confetibatchserver.domain.music.song.infra.repository;

import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SongJdbcRepositoryImpl implements SongJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String BULK_UPSERT_SONGS_SQL = """
            INSERT INTO songs (id, track_name, artwork_url, artist_name, preview_url, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, NOW(), null)
            ON DUPLICATE KEY UPDATE
                track_name = VALUES(track_name),
                artwork_url = VALUES(artwork_url),
                artist_name = VALUES(artist_name),
                preview_url = VALUES(preview_url),
                updated_at = NOW()
        """;
    private final String BULK_INSERT_ARTIST_SONGS_SQL = """
            INSERT IGNORE INTO artist_songs (song_id, artist_id)
            VALUES (?, ?)
        """;

    @Transactional
    public void upsertSongsWithArtistId(String artistId, List<ConfetiSong> songs) {

        jdbcTemplate.batchUpdate(BULK_UPSERT_SONGS_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ConfetiSong song = songs.get(i);
                ps.setString(1, song.getId());
                ps.setString(2, song.getTrackName());
                ps.setString(3, song.getArtworkUrl());
                ps.setString(4, song.getArtistName());
                ps.setString(5, song.getPreviewUrl());
            }

            @Override
            public int getBatchSize() {
                return songs.size();
            }
        });

        jdbcTemplate.batchUpdate(BULK_INSERT_ARTIST_SONGS_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ConfetiSong song = songs.get(i);
                ps.setString(1, song.getId());
                ps.setString(2, artistId);
            }

            @Override
            public int getBatchSize() {
                return songs.size();
            }
        });
    }
}
