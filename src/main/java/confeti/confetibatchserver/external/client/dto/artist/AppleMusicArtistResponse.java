package confeti.confetibatchserver.external.client.dto.artist;

import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import java.util.Optional;

public record AppleMusicArtistResponse(
    String id,
    String type,
    AppleMusicArtistAttributesResponse attributes,
    AppleMusicArtistRelationshipsResponse relationships
) {

    public ConfetiArtist toConfetiArtist() {
        String name = Optional.ofNullable(attributes)
            .map(AppleMusicArtistAttributesResponse::name)
            .orElse(null);

        String artworkUrl = Optional.ofNullable(attributes)
            .map(AppleMusicArtistAttributesResponse::artwork)
            .map(AppleMusicArtistArtworkResponse::url)
            .orElse(null);

        return ConfetiArtist.of(id, name, artworkUrl);
    }

}
