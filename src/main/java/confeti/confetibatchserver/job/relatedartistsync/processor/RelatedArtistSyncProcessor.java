package confeti.confetibatchserver.job.relatedartistsync.processor;

import confeti.confetibatchserver.external.service.MusicAPIHandler;
import confeti.confetibatchserver.job.relatedartistsync.dto.ArtistRelations;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class RelatedArtistSyncProcessor implements ItemProcessor<String, ArtistRelations> {

    private final MusicAPIHandler musicAPIHandler;

    @Override
    public ArtistRelations process(String item) throws Exception {
        List<String> allRelatedArtistIds = musicAPIHandler.getAllRelatedArtistIds(item);
        return new ArtistRelations(item, allRelatedArtistIds);
    }
}
