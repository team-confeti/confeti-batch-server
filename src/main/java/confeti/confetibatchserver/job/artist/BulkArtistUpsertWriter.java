package confeti.confetibatchserver.job.artist;

import confeti.confetibatchserver.domain.music.artist.Artist;
import confeti.confetibatchserver.domain.music.artist.application.ArtistService;
import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import confeti.confetibatchserver.external.client.AppleMusicFeignClient;
import confeti.confetibatchserver.external.client.dto.artist.AppleMusicArtistsResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
@RequiredArgsConstructor
public class BulkArtistUpsertWriter implements ItemWriter<Artist> {

    private static final String QUERY_PARAMETER_IDS_DELIMITER = ",";

    private final ArtistService artistService;
    private final AppleMusicFeignClient appleMusicFeignClient;

    @Override
    public void write(Chunk<? extends Artist> chunks) {
        List<Artist> savedArtists = new ArrayList<>(chunks.getItems());
        List<ConfetiArtist> fetchedArtists = getFetchedArtists(savedArtists);
        List<Artist> updatedArtists = artistService.getUpdatedArtists(savedArtists, fetchedArtists);
        artistService.upsertArtists(updatedArtists);

    }

    private List<ConfetiArtist> getFetchedArtists(Collection<Artist> artists) {
        String artistIds = artists.stream().map(Artist::getId)
            .collect(Collectors.joining(QUERY_PARAMETER_IDS_DELIMITER));
        AppleMusicArtistsResponse fetchedArtistResponses = appleMusicFeignClient.getArtists(
            artistIds);
        return fetchedArtistResponses.toConfetiArtists();
    }
}