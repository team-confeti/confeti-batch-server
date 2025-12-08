package confeti.confetibatchserver.external.client.dto.artist;

import confeti.confetibatchserver.domain.music.artist.Artist;
import java.util.Optional;

public record AppleMusicArtistResponse(
    String id,
    String type,
    AppleMusicArtistAttributesResponse attributes,
    AppleMusicArtistRelationshipsResponse relationships
) {

    public Artist toArtist() {
        String name = Optional.ofNullable(attributes)
            .map(AppleMusicArtistAttributesResponse::name)
            .orElse(null);

        String artworkUrl = Optional.ofNullable(attributes)
            .map(AppleMusicArtistAttributesResponse::artwork)
            .map(AppleMusicArtistArtworkResponse::url)
            .orElse(null);

        return Artist.builder()
            .id(id)
            .name(name)
            .artworkUrl(artworkUrl)
            .build();
    }

}
