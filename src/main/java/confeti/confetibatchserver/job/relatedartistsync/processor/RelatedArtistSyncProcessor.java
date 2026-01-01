package confeti.confetibatchserver.job.relatedartistsync.processor;

import confeti.confetibatchserver.external.service.MusicAPIHandler;
import confeti.confetibatchserver.job.relatedartistsync.dto.ArtistRelations;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
@RequiredArgsConstructor
public class RelatedArtistSyncProcessor implements ItemProcessor<String, ArtistRelations> {

    private final MusicAPIHandler musicAPIHandler;

    @Override
    public ArtistRelations process(String item) throws Exception {
        try {
            List<String> allRelatedArtistIds = musicAPIHandler.getAllRelatedArtistIds(item);
            return new ArtistRelations(item, allRelatedArtistIds);
        } catch (Exception e) {
            log.error("Error to sync RelatedArtist in processor,  artist id: " + item,
                e.getMessage());
            return null;
        }
    }
}
