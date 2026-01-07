package confeti.confetibatchserver.domain.music.relatedartist.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfetiRelatedArtist {

    private Long id;
    private String artistId;
    private String relatedArtistId;

    public static ConfetiRelatedArtist of(String artistId, String relatedArtistId) {
        return ConfetiRelatedArtist.builder()
            .artistId(artistId)
            .relatedArtistId(relatedArtistId)
            .build();
    }

}
