package confeti.confetibatchserver.external.client.dto.music;

import java.util.List;

public record AppleMusicMusicsResponse(
    String next,
    List<AppleMusicMusicResponse> data
) {

}
