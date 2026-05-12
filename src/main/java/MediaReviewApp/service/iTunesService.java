package MediaReviewApp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class iTunesService {
    // 日本のiTunesストアからアルバムを1件だけ検索するURL
    private static final String SEARCH_URL = "https://itunes.apple.com/search?term=%s&entity=album&limit=1&country=jp";

    public String fetchAlbumArtUrl(String title) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = String.format(SEARCH_URL, title);
            String jsonResponse = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode results = root.path("results");

            if (results.isArray() && !results.isEmpty()) {
                // artworkUrl100 がアルバムジャケット（100x100）のURLです
                return results.get(0).path("artworkUrl100").asString();
            }
        } catch (Exception e) {
            System.err.println("iTunes API連携エラー: " + e.getMessage());
        }
        return null;
    }
}