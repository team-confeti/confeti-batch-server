package confeti.confetibatchserver.domain.music.song;

import confeti.confetibatchserver.domain.music.song.vo.ConfetiSong;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "songs")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Song {

    @Id
    @Column(nullable = false)
    private String id;

    @Column(length = 100, nullable = false)
    private String trackName;

    @Column(length = 100)
    private String artistName;
 
    @Column(length = 3000)
    private String artworkUrl;

    @Column(length = 3000)
    private String previewUrl;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    private Song(String id, String trackName, String artistName, String artworkUrl,
        String previewUrl) {
        this.id = id;
        this.trackName = trackName;
        this.artistName = artistName;
        this.artworkUrl = artworkUrl;
        this.previewUrl = previewUrl;
    }

    public static Song from(ConfetiSong confetiSong) {
        return Song.builder()
            .id(confetiSong.getId())
            .trackName(confetiSong.getTrackName())
            .artistName(confetiSong.getArtistName())
            .artworkUrl(confetiSong.getArtworkUrl())
            .previewUrl(confetiSong.getPreviewUrl())
            .build();
    }

    public boolean isDifferentData(ConfetiSong confetiSong) {
        return !Objects.equals(this.artistName, confetiSong.getArtistName())
            || !Objects.equals(this.trackName, confetiSong.getTrackName())
            || !Objects.equals(this.artworkUrl, confetiSong.getArtworkUrl())
            || !Objects.equals(this.previewUrl, confetiSong.getPreviewUrl());
    }
}

