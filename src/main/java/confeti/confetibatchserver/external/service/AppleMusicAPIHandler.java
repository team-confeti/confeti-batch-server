package confeti.confetibatchserver.external.service;

import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import confeti.confetibatchserver.external.client.AppleMusicFeignClient;
import confeti.confetibatchserver.external.client.dto.music.AppleMusicMusicResponse;
import confeti.confetibatchserver.external.client.dto.music.AppleMusicMusicsResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppleMusicAPIHandler implements MusicAPIHandler {

    private final static int ARTIST_TOP_SONG_FETCH_SIZE = 100;
    private final AppleMusicFeignClient appleMusicFeignClient;

    @Override
    public List<ConfetiSong> getAllSongsByArtistId(String artistId) {
        int offset = 0;
        List<AppleMusicMusicResponse> allSongs = new ArrayList<>(100);
        String next = null;
        do {
            AppleMusicMusicsResponse response = appleMusicFeignClient.getArtistTopSongsById(
                artistId, String.valueOf(ARTIST_TOP_SONG_FETCH_SIZE), String.valueOf(offset));
            if (response.data() != null) {
                allSongs.addAll(response.data());
            }
            next = response.next();
            offset += ARTIST_TOP_SONG_FETCH_SIZE;
        } while (next != null);

        return allSongs.stream().map(AppleMusicMusicResponse::toConfetiSong).toList();
    }
}
