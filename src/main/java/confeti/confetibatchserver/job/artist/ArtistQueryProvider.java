package confeti.confetibatchserver.job.artist;

import confeti.confetibatchserver.domain.music.artist.Artist;
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
    private static final String ARTIST_NAME = "name";
    private static final String ARTIST_PROFILE_URL = "artwork_url";

    public static final RowMapper<Artist> ARTIST_MAPPER = (rs, rowNum) -> Artist.builder()
        .id(rs.getString(ARTIST_ID))
        .name(rs.getString(ARTIST_NAME))
        .artworkUrl(rs.getString(ARTIST_PROFILE_URL))
        .build();

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

}
