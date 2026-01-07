package confeti.confetibatchserver.domain.music.relatedartist.application;

import confeti.confetibatchserver.domain.music.relatedartist.infra.repository.RelatedArtistRepository;
import confeti.confetibatchserver.domain.music.relatedartist.vo.ConfetiRelatedArtist;
import confeti.confetibatchserver.job.relatedartistsync.dto.ArtistRelations;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RelatedArtistService {

    private final RelatedArtistRepository relatedArtistRepository;

    public Map<String, Set<String>> getRelatedArtistIds(Collection<String> artistIds) {
        return relatedArtistRepository.findAllConfetiRelatedArtistIdsByArtistIds(artistIds).stream()
            .collect(Collectors.groupingBy(
                ConfetiRelatedArtist::getArtistId,
                Collectors.mapping(ConfetiRelatedArtist::getRelatedArtistId, Collectors.toSet())
            ));
    }

    @Transactional
    public void reconcile(
        List<ArtistRelations> newArtistRelations
    ) {
        Map<String, Set<String>> oldRelatedArtistIdsByArtistId = getRelatedArtistIds(
            newArtistRelations.stream().map(ArtistRelations::artistId).toList());
        List<ConfetiRelatedArtist> insertRelation = new ArrayList<>();
        List<ConfetiRelatedArtist> deleteRelation = new ArrayList<>();

        newArtistRelations.forEach(newRelation -> {
            String currentArtistId = newRelation.artistId();
            Set<String> oldRelatedArtistIds = new HashSet<>(
                oldRelatedArtistIdsByArtistId.getOrDefault(currentArtistId, Set.of()));
            List<String> newRelatedArtistIds = newRelation.relatedArtistIds();
            deleteRelation.addAll(
                toDeleteRelation(currentArtistId, oldRelatedArtistIds, newRelatedArtistIds));
            insertRelation.addAll(
                toInsertRelation(currentArtistId, oldRelatedArtistIds, newRelatedArtistIds));
        });
        relatedArtistRepository.bulkInsert(insertRelation);
        relatedArtistRepository.bulkDelete(deleteRelation);
    }

    public Set<ConfetiRelatedArtist> toDeleteRelation(
        String artistId,
        Collection<String> oldRelatedArtistIds,
        Collection<String> newRelatedArtistIds
    ) {
        Set<String> deleteRelatedArtistIds = new HashSet<>(oldRelatedArtistIds);
        deleteRelatedArtistIds.removeAll(newRelatedArtistIds);

        return deleteRelatedArtistIds.stream()
            .map(relatedArtistId -> ConfetiRelatedArtist.of(artistId, relatedArtistId))
            .collect(Collectors.toSet());
    }

    public Set<ConfetiRelatedArtist> toInsertRelation(
        String artistId,
        Collection<String> oldRelatedArtistIds,
        Collection<String> newRelatedArtistIds
    ) {
        Set<String> insertRelatedArtistIds = new HashSet<>(newRelatedArtistIds);
        insertRelatedArtistIds.removeAll(oldRelatedArtistIds);

        return insertRelatedArtistIds.stream()
            .map(relatedArtistId -> ConfetiRelatedArtist.of(artistId, relatedArtistId))
            .collect(Collectors.toSet());
    }

}
