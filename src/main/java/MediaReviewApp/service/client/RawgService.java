package MediaReviewApp.service.client;

import MediaReviewApp.dto.MediaCandidateDto;
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

@Slf4j // ログを書くときに便利なlombokのライブラリ
@Service
public class RawgService {

    // これでHTTPリクエストを実行する
    private final RestTemplate restTemplate = new RestTemplate();

    // String型（つまり文字列）のJSONはそのままだと扱いにくいためこれを使ってオブジェクトにして操作しやすくする。
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${RAWG_API_KEY}")
    private String apiKey;

    public String fetchGameImageUrl(String title) {
        // キーが設定されていない場合のガード
        if ("none".equals(apiKey)) return null;

        try {
            String url = "https://api.rawg.io/api/games?key=" + apiKey + "&search=" + title;

            // ここでREST APIを叩く！結果がjsonResponseに入る。
            String jsonResponse = restTemplate.getForObject(url, String.class);

            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode results = root.path("results");

            log.info("【デバッグ】1件だけ検索するRAWG APIのリクエストURL: {}", url);

            if (results.isArray() && !results.isEmpty()) {
                // background_image がゲームのメインビジュアルURL
                return results.get(0).path("background_image").asString();
            }
        } catch (Exception e) {
            log.error("RAWG 検索中にエラーが発生しました。クエリ: {}", title, e);
        }

        return null;
    }

    public List<MediaCandidateDto> searchGames(String query) {
        List<MediaCandidateDto> candidates = new ArrayList<>();

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            // searchでタイトル検索、page_size=5 で件数制限
            String url = "https://api.rawg.io/api/games?key=" + apiKey + "&search=" + encodedQuery + "&page_size=5";

            log.info("【デバッグ】5件検索するRAWG APIのリクエストURL: {}", url);

            // iTunesの時と同様、確実な String 受け取り方式を採用
            String rawJson = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(rawJson);
            JsonNode results = root.get("results");

            if (results != null && results.isArray()) {
                for (JsonNode node : results) {
                    String title = node.has("name") ? node.get("name").asString() : "不明なゲーム";

                    // RAWGは background_image がメイン画像
                    String imageUrl = node.has("background_image") && !node.get("background_image").isNull()
                            ? node.get("background_image").asString()
                            : "/images/no-image.png";

                    String releaseDate = node.has("released") && !node.get("released").isNull()
                            ? node.get("released").asString()
                            : "-";

                    candidates.add(new MediaCandidateDto(title, imageUrl, releaseDate, "GAME"));
                }
            }
        } catch (Exception e) {
            log.error("RAWG 検索中にエラーが発生しました。クエリ: {}", query, e);
        }

        return candidates;
    }
}