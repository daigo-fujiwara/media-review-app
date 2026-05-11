package MediaReviewApp.controller;

import MediaReviewApp.service.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MediaControllerTest {

    private MediaController mediaController;
    private MediaService mediaService;

    @BeforeEach
    void setUp() {

        // 1. ServiceのモックをMockitoで作る
        mediaService = Mockito.mock(MediaService.class);

        // 2. Controllerをインスタンス化して、Serviceのモックを渡す。
        mediaController = new MediaController(mediaService);
        /*
        本番ではSpring Bootがmainメソッドでインスタンス化するが、テストでは自分でnewして
        インスタンス化する。これにより、Spring Bootの起動を待たずにテストができる。
         */
    }

    @Test
    @DisplayName("indexメソッドを呼び出したとき、戻り値が文字列 'index' になるか確認")
    void testIndex() {
        // 準備：テスト用の空のModelを用意
        Model model = new ConcurrentModel();

        // 実行：直接メソッドを叩く
        String viewName = mediaController.index(model);

        // 検証：結果が期待通り（"index"）かチェック
        assertEquals("index", viewName);
    }

    // --- ここを追加 ---
    @Test
    @DisplayName("異常時：サービスでエラーが起きたとき、戻り値が 'error' になるか確認")
    void testIndex_Error() {
        // 1. 準備：サービスが呼ばれたら例外を投げるように「演技指導」
        Mockito.when(mediaService.findByStatus(Mockito.anyString()))
                .thenThrow(new RuntimeException("テスト用エラー"));

        Model model = new ConcurrentModel();

        // 2. 実行
        String viewName = mediaController.index(model);

        // 3. 検証：戻り値が "error" になっているか
        assertEquals("error", viewName);
        // モデルにエラーメッセージが格納されているかも確認できる
        assertEquals("データの読み込みに失敗しました。", model.getAttribute("errorMessage"));
    }
}