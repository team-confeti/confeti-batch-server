package confeti.confetibatchserver.job.artist;

import confeti.confetibatchserver.api.music.facade.MusicSyncFacade;
import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
@RequiredArgsConstructor
public class BulkArtistUpsertWriter implements ItemWriter<ConfetiArtist> {

    private final MusicSyncFacade musicSyncFacade;

    @Override
    public void write(Chunk<? extends ConfetiArtist> chunks) {
        musicSyncFacade.upsertArtists(new ArrayList<>(chunks.getItems()));
    }

}