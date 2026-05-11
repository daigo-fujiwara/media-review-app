package MediaReviewApp.controller;

import MediaReviewApp.entity.Media;
import MediaReviewApp.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // 登録処理
    @PostMapping("/save")
    public String save(Media media, RedirectAttributes redirectAttributes) {
        try {
            mediaService.save(media);
            // 成功メッセージをリダイレクト先へ運ぶ
            redirectAttributes.addFlashAttribute("successMessage", "保存しました！");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "保存中にエラーが発生しました。");
            return "redirect:/";
        }
    }

    // ステータス変更（WANT -> DONE など）
    @PostMapping("/update-status")
    public String updateStatus(Long id, String status, RedirectAttributes redirectAttributes) {
        try {
            mediaService.updateStatus(id, status);
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "ステータスの更新に失敗しました。");
            return "redirect:/";
        }
    }

    // 削除
    @PostMapping("/delete")
    public String delete(Long id, RedirectAttributes redirectAttributes) {
        try {
            mediaService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "削除しました。");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "削除に失敗しました。存在しないデータの可能性があります。");
            return "redirect:/";
        }
    }
}