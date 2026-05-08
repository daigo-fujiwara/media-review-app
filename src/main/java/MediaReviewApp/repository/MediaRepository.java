package MediaReviewApp.repository;

import MediaReviewApp.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Spring Data JPAがメソッド名を解析してSQLを組み立てる。
@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    // 「WANT（観たい）」や「DONE（観た）」の状態で絞り込んで、更新順に並べる命令
    List<Media> findByStatusOrderByUpdatedAtDesc(String status);

    /*
    ちなみに以下のメソッドはわざわざ書かなくても使える
    ・save()：保存・更新する
    ・findById()：IDで1件探す
    ・findAll()：全件持ってくる
    ・deleteById()：IDを指定して消す
   */
}