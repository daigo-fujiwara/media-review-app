package MediaReviewApp.controller;

import MediaReviewApp.entity.Media;
import MediaReviewApp.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MediaController {

    @Autowired
    private MediaService mediaService;

    // メイン画面（一覧表示）
    @GetMapping("/")
    public String index(Model model) {
        // WANT（観たい）とDONE（観た）のリストをそれぞれ取得して画面に渡す
        model.addAttribute("wantList", mediaService.findByStatus("WANT"));
        model.addAttribute("doneList", mediaService.findByStatus("DONE"));
        return "index"; // index.htmlを表示
    }

    // 保存処理
    @PostMapping("/save")
    public String save(Media media) {
        mediaService.save(media);
        return "redirect:/"; // 保存が終わったらトップ画面に戻る
    }

    // 削除
    @PostMapping("/delete")
    public String delete(Long id) {
        mediaService.delete(id);
        return "redirect:/";
    }

    // ステータス変更（WANT -> DONE など）
    @PostMapping("/update-status")
    public String updateStatus(Long id, String status) {
        mediaService.updateStatus(id, status);
        return "redirect:/";
    }
}