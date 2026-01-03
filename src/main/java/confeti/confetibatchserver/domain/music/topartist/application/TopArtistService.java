package confeti.confetibatchserver.domain.music.topartist.application;

import confeti.confetibatchserver.domain.music.topartist.TopArtist;
import confeti.confetibatchserver.domain.music.topartist.infra.repository.TopArtistRepository;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopArtistService {

    private final TopArtistRepository topArtistRepository;

    @Transactional
    public void refresh(List<String> artistIds) {
        topArtistRepository.deleteAllInBatch();

        List<TopArtist> topArtists = IntStream.range(0, artistIds.size())
            .mapToObj(idx -> TopArtist.create(artistIds.get(idx), idx + 1))
            .toList();

        topArtistRepository.saveAll(topArtists);
    }
}
