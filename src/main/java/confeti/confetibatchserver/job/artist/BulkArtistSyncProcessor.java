package confeti.confetibatchserver.job.artist;

import confeti.confetibatchserver.domain.music.artist.Artist;
import confeti.confetibatchserver.external.client.AppleMusicFeignClient;
import confeti.confetibatchserver.external.client.dto.artist.AppleMusicArtistArtworkResponse;
import confeti.confetibatchserver.external.client.dto.artist.AppleMusicArtistAttributesResponse;
import confeti.confetibatchserver.external.client.dto.artist.AppleMusicArtistResponse;
import confeti.confetibatchserver.external.client.dto.artist.AppleMusicArtistsResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        List<String> artistIds = artists.stream().map(Artist::getId).toList();
        AppleMusicArtistsResponse fetchedArtists = appleMusicFeignClient.getArtists(
            String.join(QUERY_PARAMETER_IDS_DELIMITER, artistIds));

        Map<String, AppleMusicArtistResponse> fetchedArtistById = fetchedArtists.data()
            .stream()
            .collect(Collectors.toMap(AppleMusicArtistResponse::id, Function.identity()));
        List<Artist> updatedArtists = getUpdatedArtists(artists, fetchedArtistById);
        return updatedArtists;
    }

    private List<Artist> getUpdatedArtists(
        List<Artist> artists,
        Map<String, AppleMusicArtistResponse> fetchedArtistsById
    ) {
        List<Artist> updatedArtists = new ArrayList<>();
        for (Artist artist : artists) {
            AppleMusicArtistResponse artistResponse = fetchedArtistsById.get(artist.getId());

            if (artistResponse != null) {
                AppleMusicArtistAttributesResponse attributes = artistResponse.attributes();
                String artistName = attributes.name();
                String artistArtworkUrl = Optional.ofNullable(attributes.artwork())
                    .map(AppleMusicArtistArtworkResponse::url)
                    .orElse(null);

                if (artist.hasChange(artistName, artistArtworkUrl)) {
                    updatedArtists.add(artist.update(artistName, artistArtworkUrl));
                }
            }
        }
        return updatedArtists;
    }

}
