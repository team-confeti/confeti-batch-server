package confeti.confetibatchserver.job.relatedartistsync.dto;

import java.util.List;

public record ArtistRelations(
    String artistId,
    List<String> relatedArtistIds
) {

}
