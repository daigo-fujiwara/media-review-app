package MediaReviewApp.controller;

import MediaReviewApp.entity.Media;
import MediaReviewApp.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller // HTMLのパスを返すためのアノテーション
@RequiredArgsConstructor // Lombokというライブラリがコンストラクタを自動生成している
public class MediaManageController {

    // MediaService型のフィールド（クラス変数の変数）mediaServiceを作成
    private final MediaService mediaService; // finalにして変更不可にする
    /* 本来はこれを自分で書かないといけないが、@RequiredArgsConstructorで省略している
    public MediaController(MediaService mediaService) { // MediaService型のコンストラクタというメソッドの中だけで一時的に使える変数
        this.mediaService = mediaService;
    }
     */

    // 登録・変更処理
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