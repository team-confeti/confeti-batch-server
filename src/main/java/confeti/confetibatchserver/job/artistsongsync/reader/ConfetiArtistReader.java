package confeti.confetibatchserver.job.artistsongsync.reader;

import static confeti.confetibatchserver.job.artistsongsync.query.ArtistQueryProvider.CONFETI_ARTIST_MAPPER;

import confeti.confetibatchserver.domain.batch.stepconfig.StepConfig;
import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import confeti.confetibatchserver.job.artistsongsync.query.ArtistQueryProvider;
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
