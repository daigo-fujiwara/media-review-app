package MediaReviewApp.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import MediaReviewApp.service.client.GoogleBooksService;
import MediaReviewApp.service.client.RawgService;
import MediaReviewApp.service.client.TmdbService;
import MediaReviewApp.service.client.iTunesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import MediaReviewApp.entity.Media;
import MediaReviewApp.repository.MediaRepository;

@ExtendWith(MockitoExtension.class) // Mockitoを使うための宣言
class MediaServiceTest {

    @Mock // MediaRepositoryの偽物を作る（すべてのメソッドの中身が消去された状態）
    private MediaRepository mediaRepository;

    @InjectMocks // Repositoryは偽物、Serviceは本物、これで純粋にサービスのテストだけができる
    private MediaService mediaService;

    @Mock
    private TmdbService tmdbService;

    @Mock
    private GoogleBooksService googleBooksService;

    @Mock
    private iTunesService iTunesService;

    @Mock
    private RawgService rawgService;

    @Test
    @DisplayName("WANTリストの取得メソッドが、リポジトリを正しく呼び出しているか")
    void testGetWantList() {
        // 1. 準備
        // リポジトリが返してくるデータをあらかじめ作る
        List<Media> mockMediaList = new ArrayList<>();
        Media m = new Media();
        m.setTitle("千と千尋の神隠し（2001）");
        m.setStatus("WANT");
        mockMediaList.add(m);

        // リポジトリの動作を定義
        when(mediaRepository.findByStatusOrderByUpdatedAtDesc("WANT")).thenReturn(mockMediaList);

        // 2. 実行
        // サービス側のメソッドを呼び出す
        List<Media> results = mediaService.findByStatus("WANT");

        // 3. 検証
        // 期待通りのデータが返ってきているか
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getTitle()).isEqualTo("千と千尋の神隠し（2001）");

        // リポジトリのメソッドが「WANT」という引数で1回呼ばれたかをチェック
        verify(mediaRepository, times(1)).findByStatusOrderByUpdatedAtDesc("WANT");
    }

    @Test
    @DisplayName("Mediaを保存できるか")
    void testSave() {
        // 1. 準備
        Media media = new Media();
        media.setTitle("千と千尋の神隠し（2001）");

        // 2. 実行
        mediaService.save(media);

        // 3. 検証：mediaRepository.save() が、この media を引数に1回呼ばれたか？
        verify(mediaRepository, times(1)).save(media);
    }

    @Test
    @DisplayName("指定したIDで削除を指示できるか")
    void testDelete() {
        Long targetId = 1L;

        when(mediaRepository.existsById(targetId)).thenReturn(true);

        // 実行
        mediaService.delete(targetId);

        // 検証
        verify(mediaRepository, times(1)).deleteById(targetId);
    }

    @Test
    @DisplayName("MOVIEを選択したらtmdbServiceが呼び出されるか")
    void testSearchCandidates() {
        assertNotNull(mediaService.searchCandidates("テスト", "MOVIE"));
        verify(tmdbService, times(1)).searchMovieDrama("テスト", "MOVIE");

        // DRAMAのテスト（★ここを追加！）
        assertNotNull(mediaService.searchCandidates("テスト", "DRAMA"));
        verify(tmdbService, times(1)).searchMovieDrama("テスト", "DRAMA");

        // BOOKのテスト
        assertNotNull(mediaService.searchCandidates("テスト", "BOOK"));
        verify(googleBooksService, times(1)).searchBooks("テスト");

        // MUSICのテスト
        assertNotNull(mediaService.searchCandidates("テスト", "MUSIC"));
        verify(iTunesService, times(1)).searchMusic("テスト");

        // GAMEのテスト
        assertNotNull(mediaService.searchCandidates("テスト", "GAME"));
        verify(rawgService, times(1)).searchGames("テスト");

        // defaultルートのテスト
        assertTrue(mediaService.searchCandidates("テスト", "MANGA").isEmpty());

    }
}