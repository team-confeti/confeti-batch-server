package confeti.confetibatchserver.external.client.dto.music;

import java.util.List;

public record AppleMusicMusicAttributesResponse(
    String name,
    String artistName,
    AppleMusicMusicArtworkResponse artwork,
    List<AppleMusicMusicPreviewResponse> previews
) {

}
