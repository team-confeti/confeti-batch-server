package confeti.confetibatchserver.logger;

import confeti.confetibatchserver.global.exectpion.ArtistIdAwareException;
import confeti.confetibatchserver.job.artistsongsync.dto.ArtistIdWithSongs;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ArtistSongSyncSkipLogger implements SkipListener<String, Future<ArtistIdWithSongs>> {

    @Override
    public void onSkipInProcess(String item, Throwable t) {
        log.error("Error to sync ArtistSong in processor,  artist id: " + item, t);
    }

    @Override
    public void onSkipInWrite(Future<ArtistIdWithSongs> item, Throwable t) {
        if (t instanceof ArtistIdAwareException exception) {
            log.error("Error to sync ArtistSong in writer,  artist id: " +
                exception.getArtistId(), exception);
            return;
        }

        try {
            ArtistIdWithSongs artistIdWithSongs = item.get();
            log.error("Error to sync ArtistSong in writer,  artist id: " +
                artistIdWithSongs.artistId(), t);
        } catch (InterruptedException | ExecutionException e) {
            log.error(
                "Error to sync RelatedArtist in writer and failed extract artistId from future");

        }
    }
}
