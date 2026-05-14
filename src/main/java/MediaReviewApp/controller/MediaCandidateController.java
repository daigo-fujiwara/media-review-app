package MediaReviewApp.controller;

import MediaReviewApp.dto.MediaCandidateDto;
import MediaReviewApp.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // JSONを返すためのアノテーション
@RequiredArgsConstructor
public class MediaCandidateController {

    private final MediaService mediaService;

    @GetMapping("/candidate")
    public List<MediaCandidateDto> search(@RequestParam String query, @RequestParam String type) {

        // MediaService -> それぞれのServiceからの外部APIを呼び出す
        return mediaService.searchCandidates(query, type);
    }
}