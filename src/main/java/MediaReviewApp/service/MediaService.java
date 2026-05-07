package MediaReviewApp.service;

import MediaReviewApp.entity.Media;
import MediaReviewApp.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    // 全件取得
    public List<Media> findAll() {
        return mediaRepository.findAll();
    }

    // ステータス（WANT/DONE）ごとに取得
    public List<Media> findByStatus(String status) {
        return mediaRepository.findByStatusOrderByUpdatedAtDesc(status);
    }

    // 保存（新規登録・更新の両方で使います）
    public void save(Media media) {
        mediaRepository.save(media);
    }

    // 1件取得（編集画面などで使用）
    public Media findById(Long id) {
        return mediaRepository.findById(id).orElse(null);
    }

    // 削除
    public void delete(Long id) {
        mediaRepository.deleteById(id);
    }

    // MediaService.java に追加
    public void updateStatus(Long id, String status) {
        Media media = findById(id);
        if (media != null) {
            media.setStatus(status);
            mediaRepository.save(media);
        }
    }
}