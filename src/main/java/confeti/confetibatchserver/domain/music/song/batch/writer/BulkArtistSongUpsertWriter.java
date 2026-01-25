package confeti.confetibatchserver.domain.music.song.batch.writer;


import confeti.confetibatchserver.api.music.facade.MusicSyncFacade;
import confeti.confetibatchserver.job.artistsongsync.dto.ArtistIdWithSongs;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
@RequiredArgsConstructor
public class BulkArtistSongUpsertWriter implements ItemWriter<ArtistIdWithSongs> {

    private final MusicSyncFacade musicSyncFacade;

    @Override
    @SuppressWarnings("unchecked")
    public void write(Chunk<? extends ArtistIdWithSongs> chunk) throws Exception {
        List<ArtistIdWithSongs> artistIdWithSongs = (List<ArtistIdWithSongs>) chunk.getItems();
        musicSyncFacade.syncArtistsSongs(artistIdWithSongs);
    }

}
