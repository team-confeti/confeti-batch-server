package confeti.confetibatchserver.external.client.dto.artist;

public record AppleMusicArtistResponse(
    String id,
    String type,
    AppleMusicArtistAttributesResponse attributes,
    AppleMusicArtistRelationshipsResponse relationships
) {

}
