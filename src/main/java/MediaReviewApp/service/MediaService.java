package MediaReviewApp.service;

import MediaReviewApp.entity.Media;
import MediaReviewApp.repository.MediaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor // コンストラクタを自分で書く必要がなくなる
@Transactional // エラーが起きた時にデータの整合性を守る（ロールバック）
public class MediaService {

    private final MediaRepository mediaRepository;

    // ステータス（WANT/DONE）ごとに取得
    public List<Media> findByStatus(String status) {
        return mediaRepository.findByStatusOrderByUpdatedAtDesc(status);
    }

    // 保存（新規登録・更新の両方で使う）
    public void save(Media media) {
        mediaRepository.save(media);
    }

    // 1件取得（見つからない場合はエラーを投げる）
    public Media findById(Long id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("データが見つかりませんでした。ID: " + id));
    }

    // ステータス更新
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
}