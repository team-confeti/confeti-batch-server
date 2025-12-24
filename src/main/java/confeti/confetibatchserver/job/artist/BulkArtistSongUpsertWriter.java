package confeti.confetibatchserver.job.artist;


import static confeti.confetibatchserver.config.ThreadPoolConfig.MUSIC_SYNC_EXECUTOR;

import confeti.confetibatchserver.api.music.facade.MusicSyncFacade;
import confeti.confetibatchserver.domain.music.artist.Artist;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
@RequiredArgsConstructor
public class BulkArtistSongUpsertWriter implements ItemWriter<Artist> {

    private final MusicSyncFacade musicSyncFacade;

    @Qualifier(MUSIC_SYNC_EXECUTOR)
    private final Executor executor;

    @Override
    public void write(Chunk<? extends Artist> chunk) throws Exception {
        List<? extends Artist> artists = chunk.getItems();

        List<CompletableFuture<Void>> futures = artists.stream()
            .map(artist -> CompletableFuture.runAsync(() -> {
                try {
                    musicSyncFacade.upsertSongByArtistId(artist.getId());
                } catch (Exception e) {
                    // 데이터 삽입 중 오류 발생 시 로그만 남기고 다음 작업을 계속 수행
                    log.error("Error to sync ArtistSong artist: " + artist.getId(), e);
                }
            }, executor))
            .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

}
