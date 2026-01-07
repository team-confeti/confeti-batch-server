package confeti.confetibatchserver.logger;

import confeti.confetibatchserver.global.exectpion.ArtistIdAwareException;
import confeti.confetibatchserver.job.relatedartistsync.dto.ArtistRelations;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RelatedArtistSyncSkipLogger implements SkipListener<String, Future<ArtistRelations>> {

    @Override
    public void onSkipInProcess(String item, Throwable t) {
        log.error("Error to sync RelatedArtist in processor,  artist id: " + item, t);
    }

    /**
     * Processor 에서 Future 로 감싸서 응답을 주기 때문에 feignClient 의 응답에 문제가 있을 경우 artistId를 알 수 없음 따라서
     * ArtistIdAwareException 으로 wrapping 하여 문제가 발생한 artist 의 id 를 전달함
     */
    @Override
    public void onSkipInWrite(Future<ArtistRelations> item, Throwable t) {
        if (t instanceof ArtistIdAwareException exception) {
            log.error("Error to sync RelatedArtist in writer,  artist id: " +
                exception.getArtistId(), exception);
            return;
        }

        try {
            ArtistRelations artistRelations = item.get();
            log.error("Error to sync RelatedArtist in writer,  artist id: " +
                artistRelations.artistId(), t);
        } catch (InterruptedException | ExecutionException e) {
            log.error(
                "Error to sync RelatedArtist in writer and failed extract artistId from future");

        }
    }
}