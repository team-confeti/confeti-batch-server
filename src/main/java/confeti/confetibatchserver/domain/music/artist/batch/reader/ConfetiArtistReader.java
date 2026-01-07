package confeti.confetibatchserver.domain.music.artist.batch.reader;

import static confeti.confetibatchserver.domain.music.artist.batch.query.ArtistQueryProvider.CONFETI_ARTIST_MAPPER;

import confeti.confetibatchserver.domain.batch.stepconfig.StepConfig;
import confeti.confetibatchserver.domain.music.artist.batch.query.ArtistQueryProvider;
import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import javax.sql.DataSource;
import org.springframework.batch.item.database.JdbcPagingItemReader;

public class ConfetiArtistReader extends JdbcPagingItemReader<ConfetiArtist> {

    public ConfetiArtistReader(DataSource dataSource,
        StepConfig stepConfig, ArtistQueryProvider artistQueryProvider
    ) throws Exception {

        setName("confetiArtistReader");
        setDataSource(dataSource);
        setFetchSize(stepConfig.getFetchSize());
        setPageSize(stepConfig.getPageSize());
        setRowMapper(CONFETI_ARTIST_MAPPER);
        setQueryProvider(artistQueryProvider.selectAllArtists(dataSource));
    }
}
