package MediaReviewApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class RawgService {

    @Value("${RAWG_API_KEY}")
    private String apiKey;

    private static final String SEARCH_URL = "https://api.rawg.io/api/games?key=%s&search=%s&page_size=1";

    public String fetchGameImageUrl(String title) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = String.format(SEARCH_URL, apiKey, title);
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
}