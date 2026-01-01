package confeti.confetibatchserver.domain.music.artist.batch.reader;

import static confeti.confetibatchserver.domain.music.artist.batch.query.ArtistQueryProvider.ARTIST_ID_MAPPER;

import confeti.confetibatchserver.domain.batch.stepconfig.StepConfig;
import confeti.confetibatchserver.domain.music.artist.batch.query.ArtistQueryProvider;
import javax.sql.DataSource;
import org.springframework.batch.item.database.JdbcPagingItemReader;

public class ArtistIdReader extends JdbcPagingItemReader<String> {

    public ArtistIdReader(DataSource dataSource,
        StepConfig stepConfig, ArtistQueryProvider queryProvider
    ) throws Exception {

        setName("artistIdReader");
        setDataSource(dataSource);
        setFetchSize(stepConfig.getFetchSize());
        setPageSize(stepConfig.getPageSize());
        setRowMapper(ARTIST_ID_MAPPER);
        setQueryProvider(queryProvider.selectAllArtistIds(dataSource));
    }

}
