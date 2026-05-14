package MediaReviewApp.service.client;

import MediaReviewApp.dto.MediaCandidateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class RawgService {

    @Value("${RAWG_API_KEY}")
    private String apiKey;

    private static final String RAWG_BASE_URL = "https://api.rawg.io/api/games";

    public String fetchGameImageUrl(String title) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = RAWG_BASE_URL + "?key=" + apiKey + "&search=" + title;
            String jsonResponse = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode results = root.path("results");

            if (results.isArray() && !results.isEmpty()) {
                // background_image がゲームのメインビジュアルURLです
                return results.get(0).path("background_image").asString();
            }
        } catch (Exception e) {
            System.err.println("RAWG API連携エラー: " + e.getMessage());
        }
        return null;
    }

    public List<MediaCandidateDto> searchGames(String query) {
        RestTemplate restTemplate = new RestTemplate();
        List<MediaCandidateDto> candidates = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        System.out.println("★DEBUG: 現在使用中のAPIキー -> [" + apiKey + "]");

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            // searchでタイトル検索、page_size=5 で件数制限
            String url = RAWG_BASE_URL + "?key=" + apiKey + "&search=" + encodedQuery + "&page_size=5";

            System.out.println("★DEBUG: 送信URL -> [" + url + "]");

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
            System.err.println("RAWG 検索エラー: " + e.getMessage());
        }
        return candidates;
    }
}