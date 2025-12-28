package confeti.confetibatchserver.domain.music.artist;

import confeti.confetibatchserver.domain.music.artist.vo.ConfetiArtist;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "artists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Artist {

    @Id
    @Column(nullable = false)
    private String id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 3000)
    private String artworkUrl;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    private Artist(String id, String name, String artworkUrl) {
        this.id = id;
        this.name = name;
        this.artworkUrl = artworkUrl;
    }

    public static Artist from(ConfetiArtist artist) {
        return Artist.builder()
            .id(artist.getId())
            .name(artist.getName())
            .artworkUrl(artist.getProfileUrl())
            .build();
    }

}