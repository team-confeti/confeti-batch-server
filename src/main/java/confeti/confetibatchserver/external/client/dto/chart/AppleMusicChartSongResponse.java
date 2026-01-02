package confeti.confetibatchserver.external.client.dto.chart;

import confeti.confetibatchserver.external.client.dto.music.AppleMusicMusicResponse;
import java.util.List;

public record AppleMusicChartSongResponse(
    List<AppleMusicMusicResponse> data
) {

}
