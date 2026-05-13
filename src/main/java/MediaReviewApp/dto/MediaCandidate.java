package MediaReviewApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MediaCandidate {
    private String title;       // 作品名
    private String imageUrl;    // ポスター等のURL
    private String releaseDate; // 公開日や発売年（あるとユーザーが選びやすい）
}