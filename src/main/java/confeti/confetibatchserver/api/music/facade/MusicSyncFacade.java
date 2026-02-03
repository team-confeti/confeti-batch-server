package confeti.confetibatchserver.api.music.facade;

import confeti.confetibatchserver.domain.music.artist.application.ArtistService;
import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import confeti.confetibatchserver.domain.music.relatedartist.application.RelatedArtistService;
import confeti.confetibatchserver.domain.music.song.application.SongService;
import confeti.confetibatchserver.external.service.MusicAPIHandler;
import confeti.confetibatchserver.global.annotation.Facade;
import confeti.confetibatchserver.job.artistsongsync.dto.ArtistIdWithSongs;
import confeti.confetibatchserver.job.relatedartistsync.dto.ArtistRelations;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Facade
@RequiredArgsConstructor
public class MusicSyncFacade {

    private final MusicAPIHandler musicAPIHandler;
    private final ArtistService artistService;
    private final SongService songService;
    private final RelatedArtistService relatedArtistService;

    public void syncArtists(List<ConfetiArtist> artists) {
        Set<String> artistIds = artists.stream().map(ConfetiArtist::getId)
            .collect(Collectors.toSet());
        List<ConfetiArtist> fetchedArtists = musicAPIHandler.getArtistsByIds(artistIds);
        List<ConfetiArtist> updatedArtists = artistService.getUpdatedArtists(artists,
            fetchedArtists);
        artistService.upsertArtists(updatedArtists);
    }

    @Transactional
    public void syncArtistsSongs(List<ArtistIdWithSongs> artistIdWithSongs) {
        songService.upsert(artistIdWithSongs);
    }

    @Transactional
    public void syncRelatedArtists(List<ArtistRelations> newArtistRelations) {
        relatedArtistService.reconcile(newArtistRelations);
    }

    public void saveMissedArtists(Collection<String> artistId) {
        List<String> missedArtistIds = artistService.findMissedArtists(artistId);
        if (missedArtistIds.isEmpty()) {
            return;
        }

        List<ConfetiArtist> missedArtists = musicAPIHandler.getArtistsByIds(missedArtistIds);
        artistService.upsertArtists(missedArtists);
    }
}
