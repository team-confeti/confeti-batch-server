package confeti.confetibatchserver.domain.music.relatedartist.batch.processor;

import confeti.confetibatchserver.external.service.MusicAPIHandler;
import confeti.confetibatchserver.global.exectpion.ArtistIdAwareException;
import confeti.confetibatchserver.job.relatedartistsync.dto.ArtistRelations;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
@RequiredArgsConstructor
public class RelatedArtistSyncProcessor implements ItemProcessor<String, ArtistRelations> {

    private final MusicAPIHandler musicAPIHandler;

    /**
     * @param item Artist ID
     */
    @Override
    public ArtistRelations process(String item) throws Exception {
        try {
            List<String> allRelatedArtistIds = musicAPIHandler.getAllRelatedArtistIds(item);
            return new ArtistRelations(item, allRelatedArtistIds);
        } catch (Exception e) {
            throw new ArtistIdAwareException(item, e);
        }
    }
}
