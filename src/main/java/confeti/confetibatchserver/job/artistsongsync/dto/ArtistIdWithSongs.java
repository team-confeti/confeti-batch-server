package confeti.confetibatchserver.job.artistsongsync.dto;

import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import java.util.List;

public record ArtistIdWithSongs(
    String artistId,
    List<ConfetiSong> songs
) {

}
