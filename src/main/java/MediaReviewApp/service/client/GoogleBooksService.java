package MediaReviewApp.service.client;

import MediaReviewApp.dto.MediaCandidateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

@Slf4j // ログを書くときに便利なlombokのライブラリ
@Service
public class GoogleBooksService {

    // これでHTTPリクエストを実行する
    private final RestTemplate restTemplate = new RestTemplate();

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
        if ("none".equals(apiKey)) return null;

        try {
            // URLの末尾に &key= を追加してリクエスト
            String url = GOOGLE_BOOKS_API_URL + title + "&key=" + apiKey;

            log.info("【デバッグ】1件だけ検索するGoogle Books APIのリクエストURL: {}", url);

            GoogleBooksResponse response = restTemplate.getForObject(url, GoogleBooksResponse.class);

            if (response != null && response.items() != null && !response.items().isEmpty()) {
                var volumeInfo = response.items().getFirst().volumeInfo();
                if (volumeInfo.imageLinks() != null) {
                    return volumeInfo.imageLinks().thumbnail().replace("http://", "https://");
                }
            }
        } catch (Exception e) {
            log.error("Google Books 検索中にエラーが発生しました。クエリ: {}", title, e);
        }

        return null;
    }

    public List<MediaCandidateDto> searchBooks(String query) {
        List<MediaCandidateDto> candidates = new ArrayList<>();

        if ("none".equals(apiKey)) {
            return new ArrayList<>();
        }

        try {
            // maxResults=5 を指定して、上位5件を取得するように調整
            String url = GOOGLE_BOOKS_API_URL + query + "&maxResults=5&key=" + apiKey;

            log.info("【デバッグ】5件検索するGoogle Books APIのリクエストURL: {}", url);

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
            }
        } catch (Exception e) {
            log.error("Google Books 検索中にエラーが発生しました。クエリ: {}", query, e);
        }

        return candidates;
    }
}