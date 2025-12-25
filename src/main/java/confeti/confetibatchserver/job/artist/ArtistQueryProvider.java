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

    public static final RowMapper<Artist> ARTIST_MAPPER = (rs, rowNum) -> Artist.builder()
        .id(rs.getString("id"))
        .name(rs.getString("name"))
        .artworkUrl(rs.getString("artwork_url"))
        .build();

    public static final RowMapper<ConfetiArtist> CONFETI_ARTIST_MAPPER = (rs, rowNum) ->
        ConfetiArtist.of(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("artwork_url"));

    public PagingQueryProvider selectAllArtists(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);

        queryProvider.setSelectClause("id, name, artwork_url");
        queryProvider.setFromClause("FROM artists");

        Map<String, Order> sortKey = new HashMap<>();
        sortKey.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKey);

        return queryProvider.getObject();
    }

}
