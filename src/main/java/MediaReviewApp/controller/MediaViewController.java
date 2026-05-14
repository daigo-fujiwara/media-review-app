package MediaReviewApp.controller;

import MediaReviewApp.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // HTMLのパスを返すためのアノテーション
@RequiredArgsConstructor // Lombokというライブラリがコンストラクタを自動生成している
public class MediaViewController {

    // MediaService型のフィールド（クラス変数の変数）mediaServiceを作成
    private final MediaService mediaService; // finalにして変更不可にする
    /* 本来はこれを自分で書かないといけないが、@RequiredArgsConstructorで省略している
    public MediaController(MediaService mediaService) { // MediaService型のコンストラクタというメソッドの中だけで一時的に使える変数
        this.mediaService = mediaService;
    }
     */

    // メイン画面（一覧表示）
    @GetMapping("/")
    public String index(Model model) {
        try {
            // DBからStatusが "WANT" の作品を全部取ってきてリストに "wantList" というラベルを貼って箱に詰める
            model.addAttribute("wantList", mediaService.findByStatus("WANT"));
            // DBからStatusが "DONE" の作品を全部取ってきてリストに "doneList" というラベルを貼って箱に詰める
            model.addAttribute("doneList", mediaService.findByStatus("DONE"));
            return "index"; // index.htmlを表示
        } catch (Exception e) {
            model.addAttribute("errorMessage", "データの読み込みに失敗しました。");
            return "error"; // error.htmlを表示
        }
    }
}