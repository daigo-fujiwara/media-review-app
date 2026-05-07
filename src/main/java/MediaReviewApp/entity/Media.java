package MediaReviewApp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 作品タイトル
    @Column(nullable = false)
    private String title;

    // 種別 (BOOK, MOVIE)
    private String type;

    // 状態 (WANT, DONE)
    private String status;

    // 5段階評価 (1-5)
    private Integer rating;

    // 感想・レビュー
    @Column(columnDefinition = "TEXT")
    private String comment;

    // 更新日時
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}