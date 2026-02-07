package confeti.confetibatchserver.domain.music.artist.batch.reader;

import static confeti.confetibatchserver.domain.music.artist.batch.query.ArtistQueryProvider.ARTIST_ID_MAPPER;

import confeti.confetibatchserver.domain.batch.stepconfig.StepConfig;
import confeti.confetibatchserver.domain.music.artist.batch.query.ArtistQueryProvider;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.item.database.JdbcPagingItemReader;

public class ArtistIdReaderLtTargetTime extends JdbcPagingItemReader<String> {

    public ArtistIdReaderLtTargetTime(
        DataSource dataSource,
        StepConfig stepConfig,
        ArtistQueryProvider artistQueryProvider,
        LocalDateTime targetTime
    ) throws Exception {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("targetTime", targetTime);

        setName("artistIdReaderLtTargetTime");
        setDataSource(dataSource);
        setFetchSize(stepConfig.getFetchSize());
        setPageSize(stepConfig.getPageSize());
        setRowMapper(ARTIST_ID_MAPPER);

        setQueryProvider(artistQueryProvider.selectAllArtistIdsLtTargetTime(dataSource));
        setParameterValues(parameterValues);
    }
}
