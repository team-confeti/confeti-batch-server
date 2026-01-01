package confeti.confetibatchserver.job.relatedartistsync.writer;

import confeti.confetibatchserver.api.music.facade.MusicSyncFacade;
import confeti.confetibatchserver.job.relatedartistsync.dto.ArtistRelations;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class BulkRelatedArtistUpsertWriter implements ItemWriter<ArtistRelations> {

    private final MusicSyncFacade musicSyncFacade;

    @Override
    public void write(Chunk<? extends ArtistRelations> chunk) throws Exception {
        @SuppressWarnings("unchecked")
        List<ArtistRelations> relations = (List<ArtistRelations>) chunk.getItems();
        musicSyncFacade.reconcileRelatedArtists(relations);
    }
}
