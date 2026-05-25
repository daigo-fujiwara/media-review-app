package MediaReviewApp.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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
    @DisplayName("save時：MOVIEならtmdbServiceから画像URLを取得してセットできるか")
    void testSave_MovieAndDrama_SetsImageUrl() {
        // 1. 【準備】MOVIEのEntityを用意
        Media movie = new Media();
        movie.setTitle("テスト映画");
        movie.setType("MOVIE");

        // tmdbServiceが呼ばれたら、モックのURLを返すように設定
        when(tmdbService.fetchPosterUrl("テスト映画", "MOVIE")).thenReturn("https://tmdb.com/poster.jpg");

        // 2. 【実行】
        mediaService.save(movie);

        // 3. 【検証】画像がセットされ、Repositoryのsaveが呼ばれたこと
        assertEquals("https://tmdb.com/poster.jpg", movie.getImageUrl());
        verify(tmdbService, times(1)).fetchPosterUrl("テスト映画", "MOVIE");
        verify(mediaRepository, times(1)).save(movie);
    }

    @Test
    @DisplayName("save時：DRAMAならtmdbServiceから画像URLを取得してセットできるか")
    void testSave_Drama_SetsImageUrl() {
        // 1. 【準備】DRAMAのEntityを用意
        Media drama = new Media();
        drama.setTitle("テストドラマ");
        drama.setType("DRAMA");

        // DRAMAのときも tmdbService が呼ばれる設定にする
        when(tmdbService.fetchPosterUrl("テストドラマ", "DRAMA")).thenReturn("https://tmdb.com/drama_poster.jpg");

        // 2. 【実行】
        mediaService.save(drama);

        // 3. 【検証】
        assertEquals("https://tmdb.com/drama_poster.jpg", drama.getImageUrl());
        verify(tmdbService, times(1)).fetchPosterUrl("テストドラマ", "DRAMA");
        verify(mediaRepository, times(1)).save(drama);
    }

    @Test
    @DisplayName("save時：BOOKならgoogleBooksServiceから画像URLを取得してセットできるか")
    void testSave_Book_SetsImageUrl() {
        // 1. 【準備】BOOKのEntityを用意
        Media book = new Media();
        book.setTitle("テスト本");
        book.setType("BOOK");

        when(googleBooksService.fetchThumbnailUrl("テスト本")).thenReturn("https://books.com/thumb.jpg");

        // 2. 【実行】
        mediaService.save(book);

        // 3. 【検証】
        assertEquals("https://books.com/thumb.jpg", book.getImageUrl());
        verify(googleBooksService, times(1)).fetchThumbnailUrl("テスト本");
        verify(mediaRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("save時：MUSICならiTunesServiceから画像URLを取得してセットできるか")
    void testSave_Music_SetsImageUrl() {
        // 1. 【準備】MUSICのEntityを用意
        Media music = new Media();
        music.setTitle("テスト曲");
        music.setType("MUSIC");

        when(iTunesService.fetchAlbumArtUrl("テスト曲")).thenReturn("https://itunes.com/art.jpg");

        // 2. 【実行】
        mediaService.save(music);

        // 3. 【検証】
        assertEquals("https://itunes.com/art.jpg", music.getImageUrl());
        verify(iTunesService, times(1)).fetchAlbumArtUrl("テスト曲");
        verify(mediaRepository, times(1)).save(music);
    }

    @Test
    @DisplayName("save時：GAMEならrawgServiceから画像URLを取得してセットできるか")
    void testSave_Game_SetsImageUrl() {
        // 1. 【準備】GAMEのEntityを用意
        Media game = new Media();
        game.setTitle("テストゲーム");
        game.setType("GAME");

        when(rawgService.fetchGameImageUrl("テストゲーム")).thenReturn("https://rawg.com/game.jpg");

        // 2. 【実行】
        mediaService.save(game);

        // 3. 【検証】
        assertEquals("https://rawg.com/game.jpg", game.getImageUrl());
        verify(rawgService, times(1)).fetchGameImageUrl("テストゲーム");
        verify(mediaRepository, times(1)).save(game);
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
    @DisplayName("delete時：データが存在しない場合、狙い通りの例外（エラー）が発生するか")
    void testDelete_ThrowsException_WhenDataDoesNotExist() {
        Long targetId = 999L;

        // 1. 【準備】リポジトリに「データは存在しない（false）」と嘘の設定をする
        when(mediaRepository.existsById(targetId)).thenReturn(false);

        // 2. 【実行 ＆ 検証】
        // assertThrowsを使うことで、「この処理を動かしたら指定の例外が発生する？」という検証
        RuntimeException exception = assertThrows(RuntimeException.class, () -> mediaService.delete(targetId));

        // 3. 【追加検証】発生したエラーメッセージの内容が正しいかもチェック
        assertEquals("削除しようとしたデータが存在しません。ID: 999", exception.getMessage());

        // 4. 【追加検証】データがないので、削除メソッド（deleteById）は一度も【呼ばれていない】ことを証明する
        verify(mediaRepository, times(1)).existsById(targetId);
        verify(mediaRepository, never()).deleteById(targetId); // never() で「0回」を検証！
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
    @DisplayName("メディア種別に応じて正しい外部Serviceが呼び出されるか")
    void testSearchCandidates() {
        // MOVIEのテスト
        assertNotNull(mediaService.searchCandidates("テスト", "MOVIE"));
        verify(tmdbService, times(1)).searchMovieDrama("テスト", "MOVIE");

        // DRAMAのテスト
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