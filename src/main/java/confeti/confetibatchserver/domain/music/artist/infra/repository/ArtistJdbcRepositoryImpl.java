package confeti.confetibatchserver.domain.music.artist.infra.repository;

import confeti.confetibatchserver.domain.music.artist.Artist;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArtistJdbcRepositoryImpl implements ArtistJdbcRepository {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss");
    private final JdbcTemplate jdbcTemplate;
    private final String BULK_UPSERT_SQL = """
        INSERT INTO artists (id, name, artwork_url, created_at, updated_at)
        VALUES (?, ?, ?, ?, null)
        ON DUPLICATE KEY UPDATE
            name = VALUES(name),
            artwork_url = VALUES(artwork_url),
            updated_at = VALUES(created_at)
        """;

    @Override
    public void upsertArtists(Collection<Artist> artists) {
        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(FORMATTER);

        jdbcTemplate.batchUpdate(BULK_UPSERT_SQL,
            artists,
            artists.size(),
            (PreparedStatement ps, Artist artist) -> {
                ps.setString(1, artist.getId());
                ps.setString(2, artist.getName());
                ps.setString(3, artist.getArtworkUrl());
                ps.setObject(4, nowString);
            });
    }
}
