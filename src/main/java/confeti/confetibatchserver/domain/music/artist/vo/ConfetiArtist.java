package confeti.confetibatchserver.domain.music.artist.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfetiArtist {

    private String id;
    private String name;
    private String profileUrl;

    public static ConfetiArtist of(String id, String name, String profileUrl) {
        return new ConfetiArtist(id, name, profileUrl);
    }

}
