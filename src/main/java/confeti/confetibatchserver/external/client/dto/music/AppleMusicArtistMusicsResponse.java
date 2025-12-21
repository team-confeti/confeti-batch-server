package confeti.confetibatchserver.external.client.dto.music;

import java.util.List;

public record AppleMusicArtistMusicsResponse(
    String next,
    List<AppleMusicMusicResponse> data
) {

}
