package confeti.confetibatchserver.domain.music.song.dto;

import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import java.util.List;

public record ConfetiSongWithArtistIds(
    ConfetiSong song,
    List<String> artistIds
) {

}
