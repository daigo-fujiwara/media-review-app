package MediaReviewApp.repository;

import MediaReviewApp.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    // 「WANT（観たい）」や「DONE（観た）」の状態で絞り込んで、更新順に並べる命令
    List<Media> findByStatusOrderByUpdatedAtDesc(String status);
}