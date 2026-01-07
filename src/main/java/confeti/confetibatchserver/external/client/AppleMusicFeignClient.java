package confeti.confetibatchserver.external.client;

import confeti.confetibatchserver.config.AppleMusicFeignConfig;
import confeti.confetibatchserver.external.client.dto.artist.AppleMusicArtistResponse;
import confeti.confetibatchserver.external.client.dto.artist.AppleMusicArtistsResponse;
import confeti.confetibatchserver.external.client.dto.chart.AppleMusicChartsResponse;
import confeti.confetibatchserver.external.client.dto.music.AppleMusicArtistMusicsResponse;
import confeti.confetibatchserver.external.client.dto.music.AppleMusicMusicsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "AppleMusicFeignClient",
    url = "${apple-music.api.host}",
    path = "${apple-music.api.path}",
    configuration = AppleMusicFeignConfig.class
)
public interface AppleMusicFeignClient {

    @GetMapping("/artists/{id}")
    AppleMusicArtistResponse getArtistById(
        @PathVariable String id
    );

    @GetMapping("/artists")
    AppleMusicArtistsResponse getArtists(
        @RequestParam String ids
    );

    @GetMapping("/artists/{id}/view/similar-artists")
    AppleMusicArtistsResponse getRelatedArtistsById(
        @PathVariable String id,
        @RequestParam String limit,
        @RequestParam String offset
    );

    @GetMapping("/artists/{id}/view/top-songs")
    AppleMusicMusicsResponse getArtistTopSongsById(
        @PathVariable String id,
        @RequestParam String limit,
        @RequestParam String offset
    );

    @GetMapping("/artists/{id}/songs")
    AppleMusicArtistMusicsResponse getArtistSongsById(
        @PathVariable String id,
        @RequestParam String limit,
        @RequestParam String offset
    );

    @GetMapping("/songs")
    AppleMusicMusicsResponse getSongsByIds(
        @RequestParam String ids
    );

    @GetMapping("/charts")
    AppleMusicChartsResponse getCharts(
        @RequestParam String types,
        @RequestParam String limit
    );

}
