package confeti.confetibatchserver.domain.music.relatedartist.batch.writer;

import confeti.confetibatchserver.api.music.facade.MusicSyncFacade;
import confeti.confetibatchserver.job.relatedartistsync.dto.ArtistRelations;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
@RequiredArgsConstructor
public class BulkRelatedArtistUpsertWriter implements ItemWriter<ArtistRelations> {

    private final MusicSyncFacade musicSyncFacade;

    @Override
    public void write(Chunk<? extends ArtistRelations> chunk) throws Exception {
        @SuppressWarnings("unchecked")
        List<ArtistRelations> relations = (List<ArtistRelations>) chunk.getItems();
        musicSyncFacade.syncRelatedArtists(relations);
    }
}
