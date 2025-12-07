package confeti.confetibatchserver.job.artist;

import confeti.confetibatchserver.domain.music.artist.Artist;
import confeti.confetibatchserver.domain.music.artist.infra.repository.ArtistRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
@RequiredArgsConstructor
public class BulkArtistJdbcUpsertWriter implements ItemWriter<List<Artist>> {

    private final ArtistRepository artistRepository;

    @Override
    public void write(Chunk<? extends List<Artist>> chunks) {
        for (List<Artist> chunk : chunks) {
            try {
                artistRepository.saveAll(chunk);
            } catch (Exception e) {
                log.warn("[Fail] Bulk update ");
                saveOneByOne(chunk);
            }
        }
    }

    private void saveOneByOne(List<Artist> artists) {
        for (Artist artist : artists) {
            try {
                artistRepository.save(artist);
            } catch (Exception e) {
                log.error("[Fail] Save artist (ID: {}): {}", artist.getId(), e.getMessage());
            }
        }
    }

}