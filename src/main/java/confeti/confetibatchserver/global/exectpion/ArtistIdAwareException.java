package confeti.confetibatchserver.global.exectpion;

import lombok.Getter;

@Getter
public class ArtistIdAwareException extends RuntimeException {

    private final String artistId;

    public ArtistIdAwareException(String artistId, Exception e) {
        super(e);
        this.artistId = artistId;
    }
}
