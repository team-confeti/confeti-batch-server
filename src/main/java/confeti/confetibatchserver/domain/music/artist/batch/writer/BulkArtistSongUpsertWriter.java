package confeti.confetibatchserver.domain.music.artist.batch.writer;


import static confeti.confetibatchserver.config.ThreadPoolConfig.MUSIC_SYNC_EXECUTOR;

import confeti.confetibatchserver.api.music.facade.MusicSyncFacade;
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
public class BulkArtistSongUpsertWriter implements ItemWriter<String> {

    private final MusicSyncFacade musicSyncFacade;

    @Qualifier(MUSIC_SYNC_EXECUTOR)
    private final Executor executor;

    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        List<? extends String> artistIds = chunk.getItems();

        List<CompletableFuture<Void>> futures = artistIds.stream()
            .map(artistId -> CompletableFuture.runAsync(() -> {
                try {
                    musicSyncFacade.upsertSongByArtistId(artistId);
                } catch (Exception e) {
                    // 데이터 삽입 중 오류 발생 시 로그만 남기고 다음 작업을 계속 수행
                    log.error("Error to sync ArtistSong artist: " + artistId, e);
                }
            }, executor))
            .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

}
