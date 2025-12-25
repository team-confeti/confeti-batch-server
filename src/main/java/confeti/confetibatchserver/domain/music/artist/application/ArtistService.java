package confeti.confetibatchserver.domain.music.artist.application;

import confeti.confetibatchserver.domain.music.artist.Artist;
import confeti.confetibatchserver.domain.music.artist.infra.repository.ArtistRepository;
import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;

    public void upsertArtists(Collection<Artist> artists) {
        artistRepository.upsertArtists(artists);
    }

    public List<Artist> getUpdatedArtists(
        List<ConfetiArtist> savedArtists,
        List<ConfetiArtist> newArtists
    ) {
        Map<String, ConfetiArtist> savedArtistById = artistsToMap(savedArtists);

        return newArtists.stream()
            .filter(artist -> {
                ConfetiArtist savedArtist = savedArtistById.get(artist.getId());
                return savedArtist != null && savedArtist.isDifferentData(artist);
            })
            .map(Artist::from)
            .toList();
    }

    private Map<String, ConfetiArtist> artistsToMap(Collection<ConfetiArtist> artists) {
        return artists.stream()
            .collect(Collectors.toMap(
                ConfetiArtist::getId,
                Function.identity(),
                (exist, replacement) -> exist));
    }

}
