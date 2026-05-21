package MediaReviewApp.service.client;

import MediaReviewApp.dto.MediaCandidateDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TmdbService {

    // これでHTTPリクエストを実行する
    private final RestTemplate restTemplate = new RestTemplate();

    // String型（つまり文字列）のJSONはそのままだと扱いにくいためこれを使ってオブジェクトにして操作しやすくする。
    private final ObjectMapper mapper = new ObjectMapper();

    public record TmdbResponse(List<TmdbResult> results) {}

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

        // 映画なら "movie"、ドラマなら "tv" で検索
        String category = "MOVIE".equals(mediaType) ? "movie" : "tv";

        try {
            // スペースや日本語を「%20」や「%E7%B1%B3...」などの安全な文字に変換する
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);

            String url = String.format("%s/search/%s?api_key=%s&query=%s&language=ja", BASE_URL, category, apiKey, encodedTitle);

            log.info("【デバッグ】1件だけ検索するTMDB APIのリクエストURL: {}", url);

            // ここでREST APIを叩く！
            TmdbResponse jsonResponse = restTemplate.getForObject(url, TmdbResponse.class);

            // response、results、およびリストの空チェックを一気に行う
            if (jsonResponse != null && jsonResponse.results() != null && !jsonResponse.results().isEmpty()) {
                String path = jsonResponse.results().getFirst().posterPath();

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
        List<MediaCandidateDto> candidates = new ArrayList<>();

        try {
            // スペースや日本語を「%20」や「%E7%B1%B3...」などの安全な文字に変換する
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            // 映画なら "movie"、ドラマなら "tv" にカテゴリを切り替える
            String category = "DRAMA".equals(type) ? "tv" : "movie";

            String url = "https://api.themoviedb.org/3/search/" + category + "?api_key=" + apiKey + "&query=" + encodedQuery + "&language=ja-JP";

            log.info("【デバッグ】5件検索するTMDB APIのリクエストURL: {}", url);

            // ここでREST APIを叩く！
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);
            JsonNode results = root.get("results");

            if (results != null && results.isArray()) {
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
        } catch (Exception e) {
            log.error("TMDB 検索中にエラーが発生しました。クエリ: {}", query, e);
        }

        return candidates;
    }
}