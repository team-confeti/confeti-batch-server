package confeti.confetibatchserver.domain.music.song.batch.processor;

import confeti.confetibatchserver.domain.music.song.application.SongService;
import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import confeti.confetibatchserver.external.service.MusicAPIHandler;
import confeti.confetibatchserver.global.exectpion.ArtistIdAwareException;
import confeti.confetibatchserver.job.artistsongsync.dto.ArtistIdWithSongs;
import feign.FeignException.FeignClientException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
@RequiredArgsConstructor
public class ArtistSongSyncProcessor implements ItemProcessor<String, ArtistIdWithSongs> {

    private final MusicAPIHandler musicAPIHandler;
    private final SongService songService;

    /**
     * @param item Artist ID
     */
    @Override
    public ArtistIdWithSongs process(String item) throws Exception {
        try {
            List<ConfetiSong> newSongs = musicAPIHandler.getAllSongsByArtistId(item);
            Map<String, ConfetiSong> songByArtistId = songService.getConfetiSongMapByArtistId(item);
            List<ConfetiSong> upsertSongs = songService.getUpsertSongs(newSongs, songByArtistId);
            return new ArtistIdWithSongs(item, upsertSongs);
        } catch (FeignClientException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new ArtistIdAwareException(item, e);
        }
    }
}
