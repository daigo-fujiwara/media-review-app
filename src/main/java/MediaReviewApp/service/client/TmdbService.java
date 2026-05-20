package MediaReviewApp.service.client;

import MediaReviewApp.dto.MediaCandidateDto;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    public record TmdbResponse(
            List<TmdbResult> results
    ) {}

    public record TmdbResult(
            @JsonProperty("poster_path") String posterPath
    ) {}

    @Value("${TMDB_API_KEY:none}")
    private String apiKey;

    private static final String BASE_URL = "https://api.themoviedb.org/3";
    // 画像取得用のベースURL（TMDB特有の仕様）
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    public String fetchPosterUrl(String title, String mediaType) {

        if ("none".equals(apiKey)) return null;

        RestTemplate restTemplate = new RestTemplate();
        // 映画なら "movie"、ドラマなら "tv" で検索
        String category = "MOVIE".equals(mediaType) ? "movie" : "tv";

        try {
            String url = String.format("%s/search/%s?api_key=%s&query=%s&language=ja",
                    BASE_URL, category, apiKey, title);

            TmdbResponse response = restTemplate.getForObject(url, TmdbResponse.class);

            /// response、results、およびリストの空チェックを一気に行う
            if (response != null && response.results() != null && !response.results().isEmpty()) {
                String path = response.results().getFirst().posterPath();

                if (path != null) {
                    return IMAGE_BASE_URL + path;
                }
            }
        } catch (Exception e) {
            log.error("TMDB連携エラー: {} / Title: {}", e.getMessage(), title, e);
        }
        return null;
    }

    public List<MediaCandidateDto> searchMovieDrama(String query, String type) {

        // フィールドではなく、メソッド内で RestTemplate を生成（またはクラス上部で定義）
        RestTemplate restTemplate = new RestTemplate();
        // ObjectMapper も必要なのでインポートに合わせて生成
        tools.jackson.databind.ObjectMapper objectMapper = new tools.jackson.databind.ObjectMapper();

        // 映画なら "movie"、ドラマなら "tv" にカテゴリを切り替える
        String category = "DRAMA".equals(type) ? "tv" : "movie";

        String url = "https://api.themoviedb.org/3/search/" + category + "?api_key=" + apiKey
                + "&query=" + query + "&language=ja-JP";

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.get("results");

            List<MediaCandidateDto> candidates = new ArrayList<>();

            if (results != null && results.isArray()) {
                // 最大5件まで抽出
                // TmdbService.java のループ内を修正

                for (int i = 0; i < Math.min(results.size(), 5); i++) {
                    JsonNode node = results.get(i);

                    String titleKey = "tv".equals(category) ? "name" : "title";
                    String title = node.has(titleKey) ? node.get(titleKey).asString() : "不明なタイトル";

                    // 画像パスの取得
                    String path = "/images/no-image.png";
                    if (node.has("poster_path") && !node.get("poster_path").isNull()) {
                        path = "https://image.tmdb.org/t/p/w200" + node.get("poster_path").asString();
                    }

                    String dateKey = "tv".equals(category) ? "first_air_date" : "release_date";
                    String releaseDate = node.has(dateKey) ? node.get(dateKey).asString() : "-";

                    candidates.add(new MediaCandidateDto(title, path, releaseDate, type));
                }
            }
            return candidates;
        } catch (Exception e) {
            log.error("検索候補の取得中にエラーが発生しました: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}