package confeti.confetibatchserver.external.service;

import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import confeti.confetibatchserver.external.client.AppleMusicFeignClient;
import confeti.confetibatchserver.external.client.dto.artist.AppleMusicArtistsResponse;
import confeti.confetibatchserver.external.client.dto.chart.AppleMusicChartResponse;
import confeti.confetibatchserver.external.client.dto.chart.AppleMusicChartSongResponse;
import confeti.confetibatchserver.external.client.dto.chart.AppleMusicChartsResponse;
import confeti.confetibatchserver.external.client.dto.music.AppleMusicMusicResponse;
import confeti.confetibatchserver.external.client.dto.music.AppleMusicMusicsResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppleMusicAPIHandler implements MusicAPIHandler {

    private final static String QUERY_PARAMETER_IDS_DELIMITER = ",";
    private final static String SONGS_TYPE = "songs";
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

    @Override
    public List<ConfetiArtist> getArtistsByIds(Collection<String> artistIds) {
        String joinedArtistIds = String.join(QUERY_PARAMETER_IDS_DELIMITER, artistIds);
        AppleMusicArtistsResponse fetchedArtistResponses = appleMusicFeignClient.getArtists(
            joinedArtistIds);
        return fetchedArtistResponses.toConfetiArtists();
    }

    @Override
    public List<AppleMusicMusicResponse> getTopSongs(int limit) {
        AppleMusicChartsResponse chartsResponse = appleMusicFeignClient.getCharts(
            SONGS_TYPE, String.valueOf(limit));

        return Optional.ofNullable(chartsResponse.results())
            .map(AppleMusicChartResponse::songs)  
            .filter(songs -> !songs.isEmpty())  
            .map(List::getFirst)  
            .map(AppleMusicChartSongResponse::data)  
            .orElse(Collections.emptyList());
    }

    @Override
    public List<AppleMusicMusicResponse> getSongsByIds(Collection<String> songIds) {
        if (songIds.isEmpty()) {
            return Collections.emptyList();
        }
        String joinedSongIds = String.join(QUERY_PARAMETER_IDS_DELIMITER, songIds);
        AppleMusicMusicsResponse response = appleMusicFeignClient.getSongsByIds(joinedSongIds);
        return Optional.ofNullable(response.data()).orElse(Collections.emptyList());
    }
}
