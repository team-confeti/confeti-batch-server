package confeti.confetibatchserver.job.artist;

import confeti.confetibatchserver.domain.music.artist.Artist;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@RequiredArgsConstructor
public class BulkArtistJdbcUpsertWriter implements ItemWriter<List<Artist>> {

    private static final String BULK_INSERT_SQL = """
        INSERT INTO artists (id, name, artwork_url, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?) AS new_data
        ON DUPLICATE KEY UPDATE
            name = new_data.name,
            artwork_url = new_data.artwork_url,
            updated_at = new_data.updated_at
        """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends List<Artist>> chunks) {
        List<Artist> totalArtists = chunks.getItems().stream()
            .flatMap(List::stream).toList();
        if (totalArtists.isEmpty()) {
            return;
        }

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        jdbcTemplate.batchUpdate(BULK_INSERT_SQL,
            totalArtists,
            totalArtists.size(),
            (PreparedStatement ps, Artist artist) -> {
                ps.setString(1, artist.getId());
                ps.setString(2, artist.getName());
                ps.setString(3, artist.getArtworkUrl());
                ps.setTimestamp(4, now);
                ps.setTimestamp(5, now);
            });
    }
}