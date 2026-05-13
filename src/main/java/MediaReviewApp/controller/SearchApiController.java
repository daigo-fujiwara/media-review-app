package MediaReviewApp.controller;

import MediaReviewApp.dto.MediaCandidate;
import MediaReviewApp.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // JSONを返すためのアノテーション
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchApiController {

    private final MediaService mediaService;

    @GetMapping("/media")
    public List<MediaCandidate> search(@RequestParam String query, @RequestParam String type) {

        // 💡 デバッグ：ここがコンソールに出れば、フロントとの通信は成功
        System.out.println("★API受付: query=" + query + ", type=" + type);

        return mediaService.searchCandidates(query, type);
    }
}