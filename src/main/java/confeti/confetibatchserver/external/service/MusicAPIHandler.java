package confeti.confetibatchserver.external.service;

import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import confeti.confetibatchserver.external.client.dto.music.AppleMusicMusicResponse;
import java.util.Collection;
import java.util.List;

public interface MusicAPIHandler {

    List<ConfetiSong> getAllSongsByArtistId(String artistId);

    List<ConfetiArtist> getArtistsByIds(Collection<String> artistIds);

    List<AppleMusicMusicResponse> getTopSongs(int limit);

    List<AppleMusicMusicResponse> getSongsByIds(Collection<String> songIds);
}
