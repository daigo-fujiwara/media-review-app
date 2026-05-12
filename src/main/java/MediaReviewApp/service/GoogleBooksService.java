package MediaReviewApp.service;

import MediaReviewApp.dto.GoogleBooksResponse;
import org.springframework.beans.factory.annotation.Value; // これをインポート
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleBooksService {

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
}