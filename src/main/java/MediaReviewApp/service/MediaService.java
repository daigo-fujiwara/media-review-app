package MediaReviewApp.service;

import MediaReviewApp.dto.MediaCandidateDto;
import MediaReviewApp.entity.Media;
import MediaReviewApp.repository.MediaRepository;
import MediaReviewApp.service.client.GoogleBooksService;
import MediaReviewApp.service.client.RawgService;
import MediaReviewApp.service.client.TmdbService;
import MediaReviewApp.service.client.iTunesService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j // ログを書くときに便利なlombokのライブラリ
@Service
@RequiredArgsConstructor // lombokというライブラリのおかげでコンストラクタを自分で書く必要がなくなる
@Transactional // エラーが起きた時にデータの整合性を守る（ロールバック）
public class MediaService {

    private final MediaRepository mediaRepository;
    private final GoogleBooksService googleBooksService;
    private final TmdbService tmdbService;
    private final iTunesService iTunesService;
    private final RawgService rawgService;

    // ステータス（WANT/DONE）ごとに取得
    public List<Media> findByStatus(String status) {
        return mediaRepository.findByStatusOrderByUpdatedAtDesc(status);
    }

    // 保存（新規登録・更新の両方で使う）
    public void save(Media media) {

        // 保存する前に、APIで画像URLを探しに行く
        if ("MOVIE".equals(media.getType()) || "DRAMA".equals(media.getType())) {
            String imageUrl = tmdbService.fetchPosterUrl(media.getTitle(), media.getType());
            media.setImageUrl(imageUrl);
        } else if ("BOOK".equals(media.getType())) {
            String imageUrl = googleBooksService.fetchThumbnailUrl(media.getTitle());
            media.setImageUrl(imageUrl);
        } else if ("MUSIC".equals(media.getType())) {
            media.setImageUrl(iTunesService.fetchAlbumArtUrl(media.getTitle()));
        } else if ("GAME".equals(media.getType())) {
            media.setImageUrl(rawgService.fetchGameImageUrl(media.getTitle()));
        }

        mediaRepository.saveAndFlush(media);
    }

    // 下にあるupdateStatusメソッドと合わせてステータス更新
    public Media findById(Long id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("データが見つかりませんでした。ID: " + id));
    }

    public void updateStatus(Long id, String status) {
        // findByIdがエラーを投げてくれるので、ここでは正常系（見つかった場合）のことだけ書く
        Media media = findById(id);
        media.setStatus(status);
        media.setUpdatedAt(LocalDateTime.now());
        mediaRepository.save(media);
    }

    // 削除
    public void delete(Long id) {
        // 削除対象が存在するか確認してから消す
        if (!mediaRepository.existsById(id)) {
            throw new RuntimeException("削除しようとしたデータが存在しません。ID: " + id);
        }
        mediaRepository.deleteById(id);
    }

    // 予測候補
    public List<MediaCandidateDto> searchCandidates(String query, String type) {

        log.info("【デバッグ】クエリ: {}、タイプ: {}", query, type);

        return switch (type) {
            case "MOVIE", "DRAMA" -> tmdbService.searchMovieDrama(query, type);
            case "BOOK" -> googleBooksService.searchBooks(query);
            case "MUSIC" -> iTunesService.searchMusic(query);
            case "GAME" -> rawgService.searchGames(query);
            // どれにも当てはまらない文字列が来たらdefaultに引っかかり、0件の空リストを作って返す。
            default -> new ArrayList<>();
        };
    }
}