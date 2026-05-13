package MediaReviewApp.dto;

import java.util.List;

/**
 * Google Books APIからのレスポンスを格納するRecord
 */
public record GoogleBooksResponse(List<Item> items) {
    public record Item(VolumeInfo volumeInfo) {}

    public record VolumeInfo(
            String title,
            List<String> authors,
            ImageLinks imageLinks,
            String publishedDate
    ) {}

    public record ImageLinks(String thumbnail) {}
}