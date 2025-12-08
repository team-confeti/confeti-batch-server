package confeti.confetibatchserver.external.client.dto.artist;

import confeti.confetibatchserver.domain.music.artist.Artist;
import java.util.List;

public record AppleMusicArtistsResponse(
    List<AppleMusicArtistResponse> data
) {

    public List<Artist> toArtists() {
        return data.stream()
            .map(AppleMusicArtistResponse::toArtist)
            .toList();
    }
}
