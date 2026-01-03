package confeti.confetibatchserver.external.client.dto.chart;

import java.util.List;

public record AppleMusicChartResponse(
    List<AppleMusicChartSongResponse> songs
) {

}
