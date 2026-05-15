package MediaReviewApp.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Mediaを保存できるか")
    void testSave() {
        // 1. 準備
        Media media = new Media();
        media.setTitle("千と千尋の神隠し（2001）");

        // 2. 実行
        mediaService.save(media);

        // 3. 検証：mediaRepository.save() が、この media を引数に1回呼ばれたか？
        verify(mediaRepository, times(1)).saveAndFlush(media);
    }

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
    @DisplayName("存在しないIDを検索した時にエラーが投げられること")
    void testFindByIdNotFound() {
        // 存在しないであろうIDを指定してRuntimeExceptionが投げられることを検証する
        assertThrows(RuntimeException.class, () -> mediaService.findById(999L));
    }

    @Test
    @DisplayName("IDを指定してステータスを更新できるか")
    void testUpdateStatus() {
        // 準備
        Long targetId = 100L;

        // 偽物のデータ（更新前）を用意
        Media mockMedia = new Media();
        mockMedia.setId(targetId);
        mockMedia.setTitle("テスト映画");
        mockMedia.setStatus("WANT"); // 最初はWANT

        // findByIdされたら、この偽物データを返せ
        when(mediaRepository.findById(targetId)).thenReturn(java.util.Optional.of(mockMedia));

        // 2. 実行 (Act)
        mediaService.updateStatus(targetId, "DONE");

        // 3. 検証 (Assert)
        // ① ちゃんとステータスが "DONE" に書き換えられたか？
        assertThat(mockMedia.getStatus()).isEqualTo("DONE");

        // ② リポジトリの save が最終的に呼ばれたか？
        verify(mediaRepository, times(1)).save(mockMedia);
    }

    @Test
    @DisplayName("データが存在しないときは、例外が発生し、保存処理が行われないこと")
    void testUpdateStatus_NotFound() {
        Long targetId = 999L;
        when(mediaRepository.findById(targetId)).thenReturn(Optional.empty());

        // 実行部分をassertThrowsで囲む
        assertThrows(RuntimeException.class, () -> mediaService.updateStatus(targetId, "DONE"));
        verify(mediaRepository, never()).save(any());
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
}