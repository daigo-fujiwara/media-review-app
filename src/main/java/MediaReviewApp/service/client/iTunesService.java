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

@Slf4j // ログを書くときに便利なlombokのライブラリ
@Service
public class iTunesService {

    // これでHTTPリクエストを実行する
    private final RestTemplate restTemplate = new RestTemplate();

    // String型（つまり文字列）のJSONはそのままだと扱いにくいためこれを使ってオブジェクトにして操作しやすくする。
    private final ObjectMapper mapper = new ObjectMapper();

    public String fetchAlbumArtUrl(String title) {
        try {
            // スペースや日本語を「%20」や「%E7%B1%B3...」などの安全な文字に変換する
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            // 日本のiTunesストアからアルバムを1件だけ検索するURL
            String url = "https://itunes.apple.com/search?term=" + encodedTitle + "&entity=album&limit=1&country=jp";

            log.info("【デバッグ】1件だけ検索するiTunes APIのリクエストURL: {}", url);

            // ここでREST APIを叩く！結果がjsonResponseに入る。
            String jsonResponse = restTemplate.getForObject(url, String.class);

            // JSONをJsonNode型に変換！rootに保存。
            JsonNode root = mapper.readTree(jsonResponse);
            // "results"の中身だけを取ってくる
            JsonNode results = root.path("results");

            if (results.isArray() && !results.isEmpty()) {
                // 一番上の検索結果のartworkUrl100のURLをString型にして返す
                return results.get(0).path("artworkUrl100").asString();
            }
        } catch (Exception e) {
            log.error("iTunes 検索中にエラーが発生しました。クエリ: {}", title, e);
        }

        return null;
    }

    public List<MediaCandidateDto> searchMusic(String query) {
        List<MediaCandidateDto> candidates = new ArrayList<>();

        try {
            // 日本語のクエリをURLエンコードする
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://itunes.apple.com/search?term=" + encodedQuery + "&entity=song&attribute=songTerm&limit=5&country=jp&lang=ja_jp";

            log.info("【デバッグ】5件検索するiTunes APIのリクエストURL: {}", url);

            // ここでREST APIを叩く！
            String rawJson = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(rawJson);
            JsonNode results = root.get("results");

            if (results != null && results.isArray()) {
                for (JsonNode node : results) {
                    // trackName を取得してタイトルを作る
                    String trackName = node.has("trackName") ? node.get("trackName").asString() : "不明な曲名";

                    // 画像（ジャケット）
                    String imageUrl = node.has("artworkUrl100") ? node.get("artworkUrl100").asString() : "/images/no-image.png";

                    // 発売日
                    String releaseDate = "-";
                    if (node.has("releaseDate")) {
                        releaseDate = node.get("releaseDate").asString().substring(0, 10);
                    }

                    candidates.add(new MediaCandidateDto(trackName, imageUrl, releaseDate, "MUSIC"));
                }
            }
        } catch (Exception e) {
            log.error("iTunes 検索中にエラーが発生しました。クエリ: {}", query, e);
        }

        return candidates;
    }
}
