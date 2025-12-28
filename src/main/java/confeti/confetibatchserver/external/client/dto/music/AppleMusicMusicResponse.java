package confeti.confetibatchserver.external.client.dto.music;

import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import java.util.Optional;

public record AppleMusicMusicResponse(
    String id,
    String type,
    AppleMusicMusicAttributesResponse attributes,
    AppleMusicMusicRelationshipsResponse relationships
) {

    public ConfetiSong toConfetiSong() {
        Optional<AppleMusicMusicAttributesResponse> optAttributes = Optional.ofNullable(
            attributes());
        String trackName = optAttributes.map(AppleMusicMusicAttributesResponse::name).orElse(null);
        String artworkUrl = optAttributes.map(attributes ->
            attributes.artwork().url()).orElse(null);
        String artistName = optAttributes.map(AppleMusicMusicAttributesResponse::artistName)
            .orElse(null);
        String previewUrl = optAttributes.flatMap(attributes -> attributes.previews().stream()
            .findFirst()
            .map(AppleMusicMusicPreviewResponse::url)
        ).orElse(null);

        return ConfetiSong.of(id,
            trackName,
            artworkUrl,
            artistName,
            previewUrl
        );
    }
}
