package confeti.confetibatchserver.job.artist;

import confeti.confetibatchserver.domain.music.artist.Artist;
import confeti.confetibatchserver.domain.music.artist.infra.repository.ArtistRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


@RequiredArgsConstructor
public class BulkArtistJpaReader implements ItemReader<List<Artist>> {

    /**
     * url: https://developer.apple.com/documentation/applemusicapi/get-multiple-catalog-artists
     * fetch limit: 25
     */
    private static final int PAGE_SIZE = 25;
    private static final String SORT_FIELD_NAME = "id";
    private final ArtistRepository artistRepository;
    private int pageNumber = 0;

    @Override
    public List<Artist> read() {

        Page<Artist> page = artistRepository.findAll(
            PageRequest.of(pageNumber, PAGE_SIZE, Sort.by(SORT_FIELD_NAME)));

        if (page.isEmpty()) {
            return null;
        }

        pageNumber++;
        return page.getContent();
    }
}
