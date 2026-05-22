package MediaReviewApp.controller;

import MediaReviewApp.dto.MediaCandidateDto;
import MediaReviewApp.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController // JSONを返すためのアノテーション
@RequiredArgsConstructor
public class MediaCandidateController {

    private final MediaService mediaService;

    @GetMapping("/candidate")
    public List<MediaCandidateDto> search(@RequestParam String query, @RequestParam String type) {

        List<MediaCandidateDto> result = mediaService.searchCandidates(query, type);

        log.info("【デバッグ】searchCandidatesの戻り値: {}", result);

        // MediaService -> それぞれのServiceからの外部APIを呼び出す
        return result;
    }
}