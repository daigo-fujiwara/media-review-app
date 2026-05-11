package MediaReviewApp.controller;

import MediaReviewApp.entity.Media;
import MediaReviewApp.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller //Spring BootがこのクラスはWebの受付担当だと認識する
@RequiredArgsConstructor // Lombokというライブラリがコンストラクタを自動生成している
public class MediaController {

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
        // DBからStatusが "WANT" の作品を全部取ってきてリストに "wantList" というラベルを貼って箱に詰める
        model.addAttribute("wantList", mediaService.findByStatus("WANT"));
        // DBからStatusが "DONE" の作品を全部取ってきてリストに "doneList" というラベルを貼って箱に詰める
        model.addAttribute("doneList", mediaService.findByStatus("DONE"));
        return "index"; // index.htmlを表示
    }

    // 登録処理
    @PostMapping("/save")
    public String save(Media media) {
        mediaService.save(media);
        return "redirect:/"; //サーバー (Controller)がブラウザに対して"/"にGETリクエストを送り直すよう要求している
    }

    // ステータス変更（WANT -> DONE など）
    @PostMapping("/update-status")
    public String updateStatus(Long id, String status) {
        mediaService.updateStatus(id, status);
        return "redirect:/"; //サーバー (Controller)がブラウザに対して"/"にGETリクエストを送り直すよう要求している
    }

    // 削除
    @PostMapping("/delete")
    public String delete(Long id) {
        mediaService.delete(id);
        return "redirect:/"; //サーバー (Controller)がブラウザに対して"/"にGETリクエストを送り直すよう要求している
    }
}