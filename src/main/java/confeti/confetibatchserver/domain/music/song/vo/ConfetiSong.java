package confeti.confetibatchserver.domain.music.song.vo;

import confeti.confetibatchserver.domain.music.song.projection.SongProjection;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfetiSong {

    private String id;

    private String trackName;

    private String artworkUrl;

    private String artistName;

    private String previewUrl;

    public static ConfetiSong of(String id, String trackName,
        String artworkUrl, String artistName, String previewUrl
    ) {
        return new ConfetiSong(id, trackName, artworkUrl, artistName, previewUrl);
    }

    public boolean isDifferentData(SongProjection song) {
        return !Objects.equals(this.artistName, song.getArtistName())
            || !Objects.equals(this.trackName, song.getTrackName())
            || !Objects.equals(this.artworkUrl, song.getArtworkUrl())
            || !Objects.equals(this.previewUrl, song.getPreviewUrl());
    }
}
