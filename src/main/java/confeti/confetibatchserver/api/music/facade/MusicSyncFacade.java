package confeti.confetibatchserver.api.music.facade;

import confeti.confetibatchserver.domain.music.song.Song;
import confeti.confetibatchserver.domain.music.song.application.SongService;
import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import confeti.confetibatchserver.external.service.MusicAPIHandler;
import confeti.confetibatchserver.global.annotation.Facade;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class MusicSyncFacade {

    private final MusicAPIHandler musicAPIHandler;
    private final SongService songService;

    public void upsertSongByArtistId(String artistId) {
        List<ConfetiSong> fetchedSongs = musicAPIHandler.getAllSongsByArtistId(artistId);
        Map<String, Song> songMapByArtistId = songService.getSongMapByArtistId(artistId);
        List<ConfetiSong> upsertSongs = getUpsertSongs(fetchedSongs, songMapByArtistId);

        songService.upsertSongsWithArtistId(artistId, upsertSongs);
    }

    private List<ConfetiSong> getUpsertSongs(
        List<ConfetiSong> fetchedSongs,
        Map<String, Song> songMapByArtistId
    ) {
        List<ConfetiSong> upsertSongs = new ArrayList<>();
        for (ConfetiSong fetchedSong : fetchedSongs) {
            Song song = songMapByArtistId.get(fetchedSong.getId());
            if (song == null) {
                upsertSongs.add(fetchedSong);
                continue;
            }
            if (song.isDifferentData(fetchedSong)) {
                upsertSongs.add(fetchedSong);
            }
        }
        return upsertSongs;
    }
}
