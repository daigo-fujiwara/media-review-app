package MediaReviewApp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity // このクラスがデータベースの「テーブル」であることを示している。
@Data // Lombokというライブラリの機能で各フィールドに対する Getter, Setter メソッドを自動生成する。（getTitle, setTypeなど）
public class Media {

    @Id // 主キーの意味
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 連番が振られる
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

    // 画像
    @Column
    private String imageUrl; // ポスターや表紙のURL用

    @PrePersist // 初めてデータベースに保存される時に実行。
    @PreUpdate // 既存のデータが更新される時に実行。
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}