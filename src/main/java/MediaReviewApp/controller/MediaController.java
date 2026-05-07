package MediaReviewApp.controller;

import MediaReviewApp.entity.Media;
import MediaReviewApp.service.MediaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MediaController {

    private final MediaService mediaService; // finalにして変更不可にする

    // コンストラクタで受け取る
    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    // メイン画面（一覧表示）
    @GetMapping("/")
    public String index(Model model) {
        // WANT（観たい）とDONE（観た）のリストをそれぞれ取得して画面に渡す
        model.addAttribute("wantList", mediaService.findByStatus("WANT"));
        model.addAttribute("doneList", mediaService.findByStatus("DONE"));
        return "index"; // index.htmlを表示
    }

    // 登録処理
    @PostMapping("/save")
    public String save(Media media) {
        mediaService.save(media);
        //サーバー (Controller)がブラウザに対して"/"にGETリクエストを送り直すよう要求
        return "redirect:/";
    }

    // ステータス変更（WANT -> DONE など）
    @PostMapping("/update-status")
    public String updateStatus(Long id, String status) {
        mediaService.updateStatus(id, status);
        return "redirect:/";
    }

    // 削除
    @PostMapping("/delete")
    public String delete(Long id) {
        mediaService.delete(id);
        return "redirect:/";
    }
}