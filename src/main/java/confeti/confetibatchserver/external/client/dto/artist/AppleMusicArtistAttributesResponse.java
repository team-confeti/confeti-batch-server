package confeti.confetibatchserver.external.client.dto.artist;

import java.util.List;

public record AppleMusicArtistAttributesResponse(
    List<String> genreNames,
    String name,
    AppleMusicArtistArtworkResponse artwork
) {

}
