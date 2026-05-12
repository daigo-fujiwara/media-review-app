package MediaReviewApp.service;

import MediaReviewApp.dto.TmdbResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class TmdbService {

    @Value("${TMDB_API_KEY:none}")
    private String apiKey;

    private static final String BASE_URL = "https://api.themoviedb.org/3";
    // 画像取得用のベースURL（TMDB特有の仕様）
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    public String fetchPosterUrl(String title, String mediaType) {

        // デバッグ①：APIキーがちゃんと読み込めているか
        System.out.println("--- TMDB DEBUG START ---");
        System.out.println("DEBUG: apiKey = [" + apiKey + "]");
        if ("none".equals(apiKey)) return null;

        RestTemplate restTemplate = new RestTemplate();
        // 映画なら "movie"、ドラマなら "tv" で検索
        String category = "MOVIE".equals(mediaType) ? "movie" : "tv";

        try {
            String url = String.format("%s/search/%s?api_key=%s&query=%s&language=ja",
                    BASE_URL, category, apiKey, title);

            // デバッグ②：生成したURLが正しいか（これをブラウザに貼って確認できる）
            System.out.println("DEBUG: Request URL = " + url);

            TmdbResponse response = restTemplate.getForObject(url, TmdbResponse.class);

            // デバッグ③：レスポンスオブジェクト全体の状態
            if (response == null) {
                System.out.println("DEBUG: Response object is NULL");
            } else if (response.results() == null) {
                System.out.println("DEBUG: Results list is NULL");
            } else if (response.results().isEmpty()) {
                System.out.println("DEBUG: Results list is EMPTY (No match found)");
            } else {
                // デバッグ④：1件目のパスが取れているか
                String path = response.results().getFirst().posterPath(); // getFirst()でエラーが出る場合を想定
                System.out.println("DEBUG: Found poster_path = " + path);

                if (path != null) {
                    String fullUrl = IMAGE_BASE_URL + path;
                    System.out.println("DEBUG: Success! Full URL = " + fullUrl);
                    return fullUrl;
                }
            }
        } catch (Exception e) {
            log.error("TMDB連携エラーが発生しました: {}", e.getMessage());
        }
        System.out.println("--- TMDB DEBUG END ---");
        return null;
    }
}