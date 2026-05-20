package MediaReviewApp.service.client;

import MediaReviewApp.dto.MediaCandidateDto;
import lombok.extern.slf4j.Slf4j;
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
                // artworkUrl100 がアルバムジャケット（100x100）のURL
                return results.get(0).path("artworkUrl100").asString();
            }
        } catch (Exception e) {
            System.err.println("iTunes API連携エラー: " + e.getMessage());
        }
        return null;
    }

    public List<MediaCandidateDto> searchMusic(String query) {
        RestTemplate restTemplate = new RestTemplate();
        List<MediaCandidateDto> candidates = new ArrayList<>();

        // 💡 ObjectMapper は手動でインスタンス化する
        ObjectMapper mapper = new ObjectMapper();

        try {
            // 日本語のクエリをURLエンコードする
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = SEARCH_URL + encodedQuery + "&entity=song&attribute=songTerm&limit=5&country=jp&lang=ja_jp";

            String rawJson = restTemplate.getForObject(url, String.class);

            JsonNode root = mapper.readTree(rawJson);
            JsonNode results = root.get("results");

            if (results != null && results.isArray()) {
                for (JsonNode node : results) {
                    // trackName と artistName を取得してタイトルを作る
                    String trackName = node.has("trackName") ? node.get("trackName").asString() : "不明な曲名";
                    String artistName = node.has("artistName") ? node.get("artistName").asString() : "不明なアーティスト";
                    String title = trackName + " - " + artistName;

                    // 画像（ジャケット）
                    String imageUrl = node.has("artworkUrl100") ? node.get("artworkUrl100").asString() : "/images/no-image.png";

                    // 発売日
                    String releaseDate = "-";
                    if (node.has("releaseDate")) {
                        releaseDate = node.get("releaseDate").asString().substring(0, 10);
                    }

                    candidates.add(new MediaCandidateDto(title, imageUrl, releaseDate, "MUSIC"));
                }
            }
        } catch (Exception e) {
            System.err.println("iTunes 検索エラー: " + e.getMessage());
            log.error("iTunes 検索中にエラーが発生しました。クエリ: {}", query, e);
        }

        return candidates;
    }
}
