package MediaReviewApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MediaCandidate {
    private String title;
    private String imageUrl;
    private String releaseDate;
    private String type;
}