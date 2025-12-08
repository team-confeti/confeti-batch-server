package confeti.confetibatchserver.job.artist;

import confeti.confetibatchserver.domain.music.artist.Artist;
import confeti.confetibatchserver.external.client.AppleMusicFeignClient;
import confeti.confetibatchserver.external.client.dto.artist.AppleMusicArtistsResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;


@RequiredArgsConstructor
public class BulkArtistSyncProcessor implements ItemProcessor<List<Artist>, List<Artist>> {

    private static final String QUERY_PARAMETER_IDS_DELIMITER = ",";

    private final AppleMusicFeignClient appleMusicFeignClient;

    @Override
    public List<Artist> process(List<Artist> artists) {
        Map<String, Artist> fetchedArtistsById = getFetchedArtistsById(artists);

        List<Artist> updatedArtists = getUpdatedArtists(artists, fetchedArtistsById);
        return updatedArtists;
    }

    private Map<String, Artist> getFetchedArtistsById(List<Artist> artists) {
        List<Artist> fetchedArtists = getFetchedArtists(artists);
        return fetchedArtists.stream()
            .collect(Collectors.toMap(Artist::getId, Function.identity()));
    }

    private List<Artist> getFetchedArtists(List<Artist> artists) {
        List<String> artistIds = artists.stream().map(Artist::getId).toList();
        AppleMusicArtistsResponse fetchedArtistResponses = appleMusicFeignClient.getArtists(
            String.join(QUERY_PARAMETER_IDS_DELIMITER, artistIds));

        return fetchedArtistResponses.toArtists();
    }

    private List<Artist> getUpdatedArtists(
        List<Artist> artists,
        Map<String, Artist> fetchedArtistsById
    ) {
        return artists.stream()
            .map(artist -> {
                Artist fetchedArtist = fetchedArtistsById.get(artist.getId());
                if (fetchedArtist == null || !artist.isDifferent(fetchedArtist)) {
                    return null;
                }
                return artist.update(fetchedArtist);
            })
            .filter(Objects::nonNull)
            .toList();
    }

}
