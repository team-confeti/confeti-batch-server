package confeti.confetibatchserver.api.music.facade;

import confeti.confetibatchserver.domain.music.artist.Artist;
import confeti.confetibatchserver.domain.music.artist.application.ArtistService;
import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import confeti.confetibatchserver.domain.music.song.application.SongService;
import confeti.confetibatchserver.domain.music.song.projection.SongProjection;
import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import confeti.confetibatchserver.external.service.MusicAPIHandler;
import confeti.confetibatchserver.global.annotation.Facade;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class MusicSyncFacade {

    private final MusicAPIHandler musicAPIHandler;
    private final ArtistService artistService;
    private final SongService songService;

    public void upsertSongByArtistId(String artistId) {
        List<ConfetiSong> fetchedSongs = musicAPIHandler.getAllSongsByArtistId(artistId);
        Map<String, SongProjection> songMapByArtistId = songService.getConfetiSongMapByArtistId(
            artistId);
        List<ConfetiSong> upsertSongs = getUpsertSongs(fetchedSongs, songMapByArtistId);

        songService.upsertSongsWithArtistId(artistId, upsertSongs);
    }

    private List<ConfetiSong> getUpsertSongs(
        List<ConfetiSong> fetchedSongs,
        Map<String, SongProjection> songMapByArtistId
    ) {
        List<ConfetiSong> upsertSongs = new ArrayList<>();
        for (ConfetiSong fetchedSong : fetchedSongs) {
            SongProjection song = songMapByArtistId.get(fetchedSong.getId());
            if (song == null) {
                upsertSongs.add(fetchedSong);
                continue;
            }
            if (fetchedSong.isDifferentData(song)) {
                upsertSongs.add(fetchedSong);
            }
        }
        return upsertSongs;
    }

    public void upsertArtists(List<ConfetiArtist> artists) {
        Set<String> artistIds = artists.stream().map(ConfetiArtist::getId)
            .collect(Collectors.toSet());
        List<ConfetiArtist> fetchedArtists = musicAPIHandler.getArtistsByIds(artistIds);
        List<Artist> updatedArtists = artistService.getUpdatedArtists(artists, fetchedArtists);
        artistService.upsertArtists(updatedArtists);
    }

}
