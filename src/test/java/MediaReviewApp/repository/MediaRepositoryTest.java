package MediaReviewApp.repository;

import MediaReviewApp.entity.Media;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class MediaRepositoryTest {

    @Autowired
    private MediaRepository mediaRepository;

    @Test // JUnitが @Test 印のメソッドを探して実行する。
    @DisplayName("statusがWANTのものだけを、更新日の新しい順に取得できるかテスト")
    void testFindByStatusOrderByUpdatedAtDesc() {
        // 1. テスト準備　MediaにあらかじめWANT2件、DONE１件のデータを入れる
        // 1つ目のデータ（古いWANT）
        Media oldWant = new Media();
        oldWant.setTitle("千と千尋の神隠し（2001）");
        oldWant.setStatus("WANT");
        mediaRepository.save(oldWant);

        // 2つ目のデータ（新しいWANT）
        Media newWant = new Media();
        newWant.setTitle("ハウルの動く城（2004）");
        newWant.setStatus("WANT");
        mediaRepository.save(newWant);

        // 3つ目のデータ（DONE：これは取得されてはいけない）
        Media doneMedia = new Media();
        doneMedia.setTitle("もののけ姫（1997）");
        doneMedia.setStatus("DONE");
        mediaRepository.save(doneMedia);

        // 2. 実行
        List<Media> results = mediaRepository.findByStatusOrderByUpdatedAtDesc("WANT");

        // 3. 検証
        // ① WANTのデータだけが取得されているか（件数は2件のはず）
        assertThat(results).hasSize(2);

        // ② 最初（[0]番目）のデータが、後から入れた「新しい映画」になっているか
        // ※OrderByUpdatedAtDesc が正しく動いていれば、新しい方が先に来ます
        assertThat(results.getFirst().getTitle()).isEqualTo("ハウルの動く城（2004）");

        // ③ 取得されたリストの中に、DONEのステータスが混じっていないか
        assertThat(results).extracting("status").containsOnly("WANT");
    }
}
