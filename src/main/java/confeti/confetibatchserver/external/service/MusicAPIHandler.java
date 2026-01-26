package confeti.confetibatchserver.external.service;

import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import confeti.confetibatchserver.domain.music.song.dto.ConfetiSongWithArtistIds;
import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import java.util.Collection;
import java.util.List;

public interface MusicAPIHandler {

    List<ConfetiSong> getAllSongsByArtistId(final String artistId);

    List<ConfetiArtist> getArtistsByIds(final Collection<String> artistIds);

    List<String> getAllRelatedArtistIds(final String artistId);

    List<ConfetiSong> getTopSongs(int limit);

    List<ConfetiSong> getSongsByIds(final Collection<String> songIds);

    List<ConfetiSongWithArtistIds> getSongWithArtistIdsByIds(final Collection<String> songIds);
}
