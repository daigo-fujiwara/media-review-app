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

    @Mock // MediaRepositoryの偽物を作る
    private MediaRepository mediaRepository;

    @InjectMocks // Repositoryは偽物、Serviceは本物
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
        verify(mediaRepository, times(1)).save(media);
    }

    @Test
    @DisplayName("WANTリストの取得メソッドが、リポジトリを正しく呼び出しているか")
    void testGetWantList() {
        // 1. 準備 (Arrange)
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
    @DisplayName("IDを指定してステータスを更新できるか")
    void testUpdateStatus() {
        // 1. 準備 (Arrange)
        Long targetId = 100L;
        String newStatus = "DONE";

        // 偽物のデータ（更新前）を用意
        Media mockMedia = new Media();
        mockMedia.setId(targetId);
        mockMedia.setTitle("テスト映画");
        mockMedia.setStatus("WANT"); // 最初はWANT

        // 演技指導1：findByIdされたら、この偽物データを返しなさい
        // ※ Optional.of(...) で包むのがポイントです
        when(mediaRepository.findById(targetId)).thenReturn(java.util.Optional.of(mockMedia));

        // 2. 実行 (Act)
        mediaService.updateStatus(targetId, newStatus);

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

        // 変更点：実行部分をassertThrowsで囲む
        assertThrows(RuntimeException.class, () -> mediaService.updateStatus(targetId, "DONE"));
        verify(mediaRepository, never()).save(any());
    }

    @Test
    @DisplayName("指定したIDで削除を指示できるか")
    void testDelete() {
        Long targetId = 1L;

        // 変更点：この1行を追加しないと、Service内のif文で例外に飛ばされる
        when(mediaRepository.existsById(targetId)).thenReturn(true);

        mediaService.delete(targetId);

        verify(mediaRepository, times(1)).deleteById(targetId);
    }

    @Test
    @DisplayName("存在しないIDを検索した時にエラーが投げられること")
    void testFindByIdNotFound() {
        // 存在しないであろうIDを指定してRuntimeExceptionが投げられることを検証する
        assertThrows(RuntimeException.class, () -> mediaService.findById(999L));
    }
}