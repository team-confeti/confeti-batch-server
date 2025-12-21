package confeti.confetibatchserver.external.client.dto.artist;

import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import java.util.List;

public record AppleMusicArtistsResponse(
    List<AppleMusicArtistResponse> data
) {

    public List<ConfetiArtist> toConfetiArtists() {
        return data.stream()
            .map(AppleMusicArtistResponse::toConfetiArtist)
            .toList();
    }
}
