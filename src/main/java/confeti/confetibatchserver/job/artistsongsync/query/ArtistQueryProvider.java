package confeti.confetibatchserver.job.artistsongsync.query;

import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class ArtistQueryProvider {

    private static final String ARTIST_ID = "id";
    public static final RowMapper<String> ARTIST_ID_MAPPER = ((rs, rowNum) -> rs.getString(
        ARTIST_ID));
    private static final String ARTIST_NAME = "name";
    private static final String ARTIST_PROFILE_URL = "artwork_url";
    public static final RowMapper<ConfetiArtist> CONFETI_ARTIST_MAPPER = (rs, rowNum) ->
        ConfetiArtist.of(
            rs.getString(ARTIST_ID),
            rs.getString(ARTIST_NAME),
            rs.getString(ARTIST_PROFILE_URL));

    public PagingQueryProvider selectAllArtists(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);

        queryProvider.setSelectClause("id, name, artwork_url");
        queryProvider.setFromClause("FROM artists");

        Map<String, Order> sortKey = new HashMap<>();
        sortKey.put(ARTIST_ID, Order.ASCENDING);
        queryProvider.setSortKeys(sortKey);

        return queryProvider.getObject();
    }

    public PagingQueryProvider selectAllArtistIds(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);

        queryProvider.setSelectClause("id");
        queryProvider.setFromClause("FROM artists");

        Map<String, Order> sortKey = new HashMap<>();
        sortKey.put(ARTIST_ID, Order.ASCENDING);
        queryProvider.setSortKeys(sortKey);

        return queryProvider.getObject();
    }

}
