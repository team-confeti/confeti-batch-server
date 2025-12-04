package confeti.confetibatchserver.external.client.dto.music;

public record AppleMusicMusicResponse(
    String id,
    String type,
    AppleMusicMusicAttributesResponse attributes,
    AppleMusicMusicRelationshipsResponse relationships
) {

}
