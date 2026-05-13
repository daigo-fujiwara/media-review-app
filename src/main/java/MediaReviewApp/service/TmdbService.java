package MediaReviewApp.service;

import MediaReviewApp.dto.MediaCandidate;
import MediaReviewApp.dto.TmdbResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

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

    public List<MediaCandidate> searchCandidates(String query, String type) {

        System.out.println("★Javaに届いた検索ワード: " + query);

        // フィールドではなく、メソッド内で RestTemplate を生成（またはクラス上部で定義）
        RestTemplate restTemplate = new RestTemplate();
        // ObjectMapper も必要なのでインポートに合わせて生成
        tools.jackson.databind.ObjectMapper objectMapper = new tools.jackson.databind.ObjectMapper();

        // 映画なら "movie"、ドラマなら "tv" にカテゴリを切り替える
        String category = "DRAMA".equals(type) ? "tv" : "movie";

        String url = "https://api.themoviedb.org/3/search/" + category + "?api_key=" + apiKey
                + "&query=" + query + "&language=ja-JP";

        try {
            // 修正ポイント：二重定義（String response = ...）を1つに整理
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.get("results");

            List<MediaCandidate> candidates = new ArrayList<>();

            if (results != null && results.isArray()) {
                // 最大5件まで抽出
                // TmdbService.java のループ内を修正

                for (int i = 0; i < Math.min(results.size(), 5); i++) {
                    JsonNode node = results.get(i);

                    // 💡 修正ポイント1：ドラマなら "name"、映画なら "title" を取得する
                    String titleKey = "tv".equals(category) ? "name" : "title";
                    String title = node.has(titleKey) ? node.get(titleKey).asString() : "不明なタイトル";

                    // 画像パスの取得
                    String path = "/images/no-image.png";
                    if (node.has("poster_path") && !node.get("poster_path").isNull()) {
                        path = "https://image.tmdb.org/t/p/w200" + node.get("poster_path").asString();
                    }

                    // 💡 修正ポイント2：ドラマなら "first_air_date"、映画なら "release_date" を取得する
                    String dateKey = "tv".equals(category) ? "first_air_date" : "release_date";
                    String releaseDate = node.has(dateKey) ? node.get(dateKey).asString() : "-";

                    candidates.add(new MediaCandidate(title, path, releaseDate));
                }
            }
            return candidates;
        } catch (Exception e) {
            log.error("検索候補の取得中にエラーが発生しました: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}