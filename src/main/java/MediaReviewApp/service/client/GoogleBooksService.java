package MediaReviewApp.service.client;

import MediaReviewApp.dto.MediaCandidateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleBooksService {

    public record GoogleBooksResponse(List<Item> items) {
        public record Item(VolumeInfo volumeInfo) {}

        public record VolumeInfo(
                String title,
                ImageLinks imageLinks,
                String publishedDate
        ) {}

        public record ImageLinks(String thumbnail) {}
    }

    // 環境変数 GOOGLE_BOOKS_API_KEY から値を読み込む
    // 設定されていない場合はデフォルト値として "none" を代入する
    @Value("${GOOGLE_BOOKS_API_KEY:none}")
    private String apiKey;

    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=intitle:";

    @SuppressWarnings("HttpUrlsUsage")
    public String fetchThumbnailUrl(String title) {
        // キーが設定されていない場合のガード
        if ("none".equals(apiKey)) {
            System.err.println("APIキーが設定されていないため、連携をスキップします。");
            return null;
        }

        RestTemplate restTemplate = new RestTemplate();
        try {
            // URLの末尾に &key= を追加してリクエスト
            String url = GOOGLE_BOOKS_API_URL + title + "&key=" + apiKey;
            GoogleBooksResponse response = restTemplate.getForObject(url, GoogleBooksResponse.class);

            if (response != null && response.items() != null && !response.items().isEmpty()) {
                var volumeInfo = response.items().getFirst().volumeInfo();
                if (volumeInfo.imageLinks() != null) {
                    return volumeInfo.imageLinks().thumbnail().replace("http://", "https://");
                }
            }
        } catch (Exception e) {
            System.err.println("API連携エラー: " + e.getMessage());
        }
        return null;
    }

    /**
     * タイトルから本の候補を複数検索する（サジェスト用）
     */
    public List<MediaCandidateDto> searchBooks(String title) {

        if ("none".equals(apiKey)) {
            return new ArrayList<>();
        }

        RestTemplate restTemplate = new RestTemplate();
        List<MediaCandidateDto> candidates = new ArrayList<>();

        try {
            // maxResults=5 を指定して、上位5件を取得するように調整
            String url = GOOGLE_BOOKS_API_URL + title + "&maxResults=5&key=" + apiKey;
            GoogleBooksResponse response = restTemplate.getForObject(url, GoogleBooksResponse.class);

            if (response != null && response.items() != null) {
                for (var item : response.items()) {
                    var info = item.volumeInfo();

                    String imageUrl = (info.imageLinks() != null) ? info.imageLinks().thumbnail() : null;

                    // フロントエンドが必要とする形式に詰め替え
                    candidates.add(new MediaCandidateDto(
                            info.title(),
                            imageUrl,
                            info.publishedDate(),
                            "BOOK"
                    ));
                }
            } else {
                System.out.println("★データが空またはレスポンスがNULLです");
            }
        } catch (Exception e) {
            System.err.println("Google Books 検索エラー: " + e.getMessage());
        }
        return candidates;
    }
}